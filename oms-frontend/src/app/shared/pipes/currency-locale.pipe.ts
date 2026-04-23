import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'currencyLocale',
  standalone: true
})
export class CurrencyLocalePipe implements PipeTransform {
  transform(value: number): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(value);
  }
}
