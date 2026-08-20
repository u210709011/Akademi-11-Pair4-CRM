import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideTranslateService } from '@ngx-translate/core';

import { DetailCustomerComponent } from './detail-customer.component';

describe('DetailCustomerComponent', () => {
  let component: DetailCustomerComponent;
  let fixture: ComponentFixture<DetailCustomerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetailCustomerComponent],
      providers: [provideTranslateService()]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DetailCustomerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
