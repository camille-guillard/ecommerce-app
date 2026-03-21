import { Component, EventEmitter, Input, Output, signal } from '@angular/core';

@Component({
  selector: 'app-star-rating',
  template: `
    <span class="star-rating" [class.interactive]="interactive" [class.disabled]="disabled">
      @for (star of stars; track star) {
        <span
          class="star"
          [class.full]="getStarType(star) === 'full'"
          [class.half]="getStarType(star) === 'half'"
          [class.empty]="getStarType(star) === 'empty'"
          [class.hover]="interactive && hoverValue() >= star"
          (mouseenter)="onHover(star)"
          (mouseleave)="onLeave()"
          (click)="onSelect(star)"
        >&#9733;</span>
      }
    </span>
  `,
  styles: [`
    .star-rating { display: inline-flex; gap: 1px; }
    .star {
      font-size: 1.2em;
      color: #d1d5db;
      transition: color 0.15s;
      user-select: none;
    }
    .star.full { color: #f59e0b; }
    .star.half {
      background: linear-gradient(90deg, #f59e0b 50%, #d1d5db 50%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
    }
    .interactive .star { cursor: pointer; }
    .interactive .star.hover { color: #fbbf24; }
    .disabled .star { opacity: 0.4; cursor: not-allowed; }
  `],
})
export class StarRatingComponent {
  @Input() rating = 0;
  @Input() interactive = false;
  @Input() disabled = false;
  @Output() ratingChange = new EventEmitter<number>();

  stars = [1, 2, 3, 4, 5];
  hoverValue = signal(0);

  getStarType(star: number): string {
    const value = this.interactive && this.hoverValue() > 0 ? this.hoverValue() : this.rating;
    if (star <= Math.floor(value)) return 'full';
    if (star - 0.5 <= value) return 'half';
    return 'empty';
  }

  onHover(star: number): void {
    if (this.interactive && !this.disabled) {
      this.hoverValue.set(star);
    }
  }

  onLeave(): void {
    this.hoverValue.set(0);
  }

  onSelect(star: number): void {
    if (this.interactive && !this.disabled) {
      this.rating = star;
      this.ratingChange.emit(star);
    }
  }
}
