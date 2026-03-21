import { Injectable, signal } from '@angular/core';

export interface Toast {
  message: string;
  id: number;
}

@Injectable({ providedIn: 'root' })
export class ToastService {
  private nextId = 0;
  readonly toasts = signal<Toast[]>([]);

  show(message: string, duration = 2500): void {
    const id = this.nextId++;
    this.toasts.set([...this.toasts(), { message, id }]);
    setTimeout(() => {
      this.toasts.set(this.toasts().filter(t => t.id !== id));
    }, duration);
  }
}
