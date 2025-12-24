import { Injectable, ApplicationRef, ComponentRef, createComponent, EnvironmentInjector } from '@angular/core';
import { ToastComponent } from './toast-component';

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  private toastComponentRef?: ComponentRef<ToastComponent>;

  constructor(
    private appRef: ApplicationRef,
    private injector: EnvironmentInjector
  ) {
    this.initializeToastComponent();
  }

  private initializeToastComponent() {
    // Create the toast component
    this.toastComponentRef = createComponent(ToastComponent, {
      environmentInjector: this.injector
    });

    // Attach to application
    this.appRef.attachView(this.toastComponentRef.hostView);

    // Append to body
    const domElem = (this.toastComponentRef.hostView as any).rootNodes[0] as HTMLElement;
    document.body.appendChild(domElem);
  }

  success(message: string, duration: number = 3000) {
    this.toastComponentRef?.instance.success(message, duration);
  }

  error(message: string, duration: number = 4000) {
    this.toastComponentRef?.instance.error(message, duration);
  }

  warning(message: string, duration: number = 3500) {
    this.toastComponentRef?.instance.warning(message, duration);
  }

  info(message: string, duration: number = 3000) {
    this.toastComponentRef?.instance.info(message, duration);
  }

  show(message: string, type: 'success' | 'error' | 'warning' | 'info' = 'info', duration: number = 3000) {
    this.toastComponentRef?.instance.show(message, type, duration);
  }
}