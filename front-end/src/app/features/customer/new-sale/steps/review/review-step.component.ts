import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { I18nService } from '../../../../../core/i18n';
import { NewSaleFormStateService } from '../../new-sale.component';

// Business Interaction ID mockup'ta var ama OrderSummaryResponse'da hic donmuyor (BsnInter entity
// backend'de olusuyor ama disariya expose edilmiyor) - kullanicinin karariyla bu alan gosterilmiyor.
@Component({
  selector: 'app-review-step',
  imports: [DecimalPipe],
  templateUrl: './review-step.component.html',
  styleUrl: './review-step.component.scss',
  changeDetection: ChangeDetectionStrategy.Eager
})
export class ReviewStepComponent {
  protected readonly i18n = inject(I18nService);
  protected readonly formState = inject(NewSaleFormStateService);

  protected readonly orderDate = new Date().toLocaleDateString('tr-TR');

  protected readonly totalAmount = computed(() =>
    this.formState.basket().reduce((sum, line) => sum + line.price, 0)
  );
}
