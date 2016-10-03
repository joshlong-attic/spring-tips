package com.example;

import org.apache.commons.logging.LogFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.filesystem.nativefs.NativeFileSystemFactory;
import org.apache.ftpserver.ftplet.*;
import org.apache.ftpserver.listener.Listener;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.impl.TransferRatePermission;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.util.Assert.isTrue;


@SpringBootApplication
public class FtpServerApplication {

    @Configuration
    public static class FtpServerConfiguration {

        @Bean
        UserManager userManager(FtpUserRepository r, @Value("${ftp.root:${HOME}/Desktop/ftp}") File root) {
            isTrue(root.exists() || root.mkdirs());
            return new FtpUserManager(r, root);
        }

        @Bean
        FileSystemFactory fileSystemFactory() {
            NativeFileSystemFactory fileSystemFactory = new NativeFileSystemFactory();
            fileSystemFactory.setCreateHome(true);
            fileSystemFactory.setCaseInsensitive(false);
            return fileSystemFactory;
        }

        @Bean
        Listener nioListener(@Value("${ftp.port:2121}") int port) throws Exception {
            ListenerFactory listenerFactory = new ListenerFactory();
            listenerFactory.setPort(port);
            return listenerFactory.createListener();
        }

        @Bean
        FtpServer ftpServer(UserManager userManager, Listener nioListener, FileSystemFactory fileSystemFactory) throws FtpException {
            FtpServerFactory ftpServerFactory = new FtpServerFactory();
            ftpServerFactory.setListeners(Collections.singletonMap("default", nioListener));
            ftpServerFactory.setFileSystem(fileSystemFactory);
            ftpServerFactory.setUserManager(userManager);
            return ftpServerFactory.createServer();
        }

        @Bean
        DisposableBean destroyFtpServer(FtpServer ftpServer) {
            return ftpServer::stop;
        }

        @Bean
        InitializingBean startFtpServer(FtpServer ftpServer) throws Exception {
            return ftpServer::start;
        }
    }

    @Bean
    CommandLineRunner data(FtpUserRepository repository) {
        return args -> Stream.of("jlong,spring", "pwebb,boot", "dsyer,cloud")
                .map(x -> x.split(","))
                .forEach(x -> repository.save(new FtpUser( "ws", x[0], x[1], true)));
    }

    public static void main(String[] args) {
        SpringApplication.run(FtpServerApplication.class, args);
    }
}

interface FtpUserRepository extends JpaRepository<FtpUser, String> {

    Optional<FtpUser> findByUsername(String u);
}

class FtpUserManager implements UserManager {

    private static final AtomicReference<FtpUserManager> ROOT_FS = new AtomicReference<>();

    private final FtpUserRepository ftpUserRepository;

    private final File rootFileSystem;

    FtpUserManager(FtpUserRepository ftpUserRepository, File rootFileSystem) {
        this.ftpUserRepository = ftpUserRepository;
        this.rootFileSystem = rootFileSystem;
        ROOT_FS.compareAndSet(null, this);
    }

    static File getHomeDirectory(String username) {
        FtpUserManager ftpUserManager = ROOT_FS.get();
        return ftpUserManager.getHomeDirectoryFor(username);
    }

    private File getHomeDirectoryFor(String username) {
        return this.ftpUserRepository.findByUsername(username)
                .map(u -> new File(this.rootFileSystem, u.getWorkspace()))
                .orElseThrow(() -> new IllegalArgumentException(username + " doesn't exist"));
    }

    @Override
    public User getUserByName(String s) throws FtpException {
        return this.ftpUserRepository.findByUsername(s).orElse(null);
    }

    @Override
    public String[] getAllUserNames() throws FtpException {
        List<String> collect = this.ftpUserRepository.findAll()
                .stream()
                .map(FtpUser::getName)
                .collect(Collectors.toList());
        return collect.toArray(new String[collect.size()]);
    }

    @Override
    public void delete(String s) throws FtpException {
        this.ftpUserRepository.findByUsername(s).ifPresent(this.ftpUserRepository::delete);
    }

    @Override
    public void save(User user) throws FtpException {
        Class<FtpUser> ftpUserClass = FtpUser.class;
        Assert.isInstanceOf(ftpUserClass, user);
        FtpUser ftpUser = ftpUserClass.cast(user);
        this.ftpUserRepository.save(ftpUser);
    }

    @Override
    public boolean doesExist(String s) throws FtpException {
        return this.ftpUserRepository.findByUsername(s).isPresent();
    }

    private void assertAboutAuthentication(boolean condition, String msg) throws AuthenticationFailedException {
        if (!condition) throw new AuthenticationFailedException(msg);
    }

    @Override
    public User authenticate(Authentication authentication) throws AuthenticationFailedException {
        Class<UsernamePasswordAuthentication> authenticationClass = UsernamePasswordAuthentication.class;
        assertAboutAuthentication(authenticationClass.isAssignableFrom(authentication.getClass()),
                "you must login using a username and password");
        UsernamePasswordAuthentication auth = authenticationClass.cast(authentication);
        assertAboutAuthentication(StringUtils.hasText(auth.getUsername()), "you must provide a username");
        assertAboutAuthentication(StringUtils.hasText(auth.getPassword()), "you must provide a password");
        return this.ftpUserRepository.findByUsername(auth.getUsername())
                .filter(u -> u.getPassword().equals(auth.getPassword()))
                .orElseThrow(() -> new AuthenticationFailedException(
                        "the provided username and password is invalid"));
    }

    @Override
    public String getAdminName() throws FtpException {
        return "admin";
    }

    @Override
    public boolean isAdmin(String s) throws FtpException {
        return getAdminName().equals(s);
    }
}


@Entity
@Table(name = "FTP_USER")
class FtpUser implements User {

    @Transient
    private List<Authority> adminAuthorities = Collections.singletonList(new WritePermission());

    @Transient
    private List<Authority> anonAuthorities = Arrays.asList(
            new ConcurrentLoginPermission(20, 2),
            new TransferRatePermission(4800, 4800));

    @Id
    private String username;
    private String workspace; // users in the same workspace will be able to share the same file system
    private boolean admin;
    private String password;
    private int maxIdleTime = 0; // no limit
    private boolean enabled;

    FtpUser() {
    }

    public FtpUser(String ws, String username, String password, boolean admin) {
        this(ws, username, password, admin, 0, true);
    }

    public FtpUser(String workspace, String username, String password, boolean admin,
                   int maxIdleTime, boolean enabled) {
        this.username = username;
        this.workspace = workspace;
        this.admin = admin;
        this.password = password;
        this.maxIdleTime = maxIdleTime;
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "FtpUser{" + "adminAuthorities=" + adminAuthorities +
                ", anonAuthorities=" + anonAuthorities +
                ", username='" + username + '\'' +
                ", workspace='" + workspace + '\'' +
                ", admin=" + admin +
                ", password='" + "*****" + '\'' +
                ", maxIdleTime=" + maxIdleTime +
                ", enabled=" + enabled +
                '}';
    }

    public String getWorkspace() {
        return workspace;
    }

    @Override
    public String getName() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public List<Authority> getAuthorities() {
        List<Authority> authorities = new ArrayList<>();
        authorities.addAll(this.anonAuthorities);
        if (this.admin) {
            authorities.addAll(this.adminAuthorities);
        }
        return authorities;
    }

    @Override
    public List<Authority> getAuthorities(Class<? extends Authority> aClass) {
        return this.getAuthorities().stream()
                .filter(a -> a.getClass().equals(aClass))
                .collect(Collectors.toList());
    }

    @Override
    public AuthorizationRequest authorize(AuthorizationRequest authorizationRequest) {
        return this.getAuthorities()
                .stream()
                .filter(a -> a.canAuthorize(authorizationRequest))
                .map(a -> a.authorize(authorizationRequest))
                .filter(a -> a != null)
                .findFirst()
                .orElse(null);
    }

    @Override
    public int getMaxIdleTime() {
        return this.maxIdleTime;
    }

    @Override
    public boolean getEnabled() {
        return this.enabled;
    }

    @Override
    public String getHomeDirectory() {
        String homeDir = FtpUserManager.getHomeDirectory(this.username)
                .getAbsolutePath();
        LogFactory.getLog(getClass()).info("home-directory: " + homeDir);
        return homeDir;
    }
}