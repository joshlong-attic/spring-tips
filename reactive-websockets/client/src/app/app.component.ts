import {Component} from '@angular/core';

@Component({
  selector: 'app-root',
  template: `
    <h1>Files</h1>
    <ol>
      <li *ngFor="let f of files">{{f.path}} / {{f.sessionId}}</li>
    </ol>
  `
})
export class AppComponent {

  private ws = new WebSocket('ws://localhost:8080/ws/files');

  files: Array<FileEvent> = [];

  constructor() {

    this.ws.onmessage = (me: MessageEvent) => {
      const fe = JSON.parse(me.data) as FileEvent;
      this.files.push(fe);
      console.log(JSON.stringify(fe));
    };
  }
}

export interface FileEvent {
  sessionId: string;
  path: string;
}
