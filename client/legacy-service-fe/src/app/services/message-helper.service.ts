import { ErrorHandler, Component, Inject, Injectable, Injector, signal, WritableSignal } from '@angular/core';
import { form } from '@angular/forms/signals';
import { toObservable } from '@angular/core/rxjs-interop';

enum MsgType {
  Success = 1,
  Info = 2,
  Warning = 3,
  Error = 4,
};

@Injectable({
  providedIn: 'root'
})
export class MessageHelperService {

  public readonly toasts: WritableSignal<{ type: MsgType, message: string }[]> = signal([]);
  public readonly toasts$ = toObservable(this.toasts);

  msg_type: typeof MsgType = MsgType;

  info(message: string) {
    this.messageShow(MsgType.Info, message);
  }

  messageShow(msgType: MsgType, message: string) {
    console.log('MessageHelperService adding message[1]:', message, this.toasts());
    this.toasts.update(
      (values) => {
        let newMessage: string = new Date().toLocaleString() + " - " + message;
        return [...values, { type: msgType, message: newMessage }];
      }
    );
    console.log('MessageHelperService adding message[2]:', this.toasts());
  }

  error(message: string) {
    this.messageShow(MsgType.Error, message);
  }

  remove(index: number) {
    this.toasts().splice(index, 1);
  }
  removeAll() {
    this.toasts().splice(0, this.toasts().length);
  }
}
