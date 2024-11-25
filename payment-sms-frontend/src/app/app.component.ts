import { Component } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { PaymentResponse, PaymentService } from './service/payment.service';
import { CommonModule } from '@angular/common';
import { AngularToastifyModule, ToastService } from 'angular-toastify';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule, AngularToastifyModule, ReactiveFormsModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  isPaying: boolean = false;
  payForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private service: PaymentService,
    private toast: ToastService
  ) {
    this.payForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      amount: ['', [Validators.required, Validators.pattern('^[0-9]+(\\.[0-9]{1,2})?$')]]
    });
  }

  payNow() {
    if (this.payForm.valid) {
      const paymentData = this.payForm.value;
      console.log(paymentData)
      this.isPaying = true;

      this.service.payNow(this.payForm.value.email,this.payForm.value.amount).subscribe(
        (response: PaymentResponse) => {
          if (response.status) {
            this.toast.success(response.message + ": You are being redirected to payment page");

            window.location.href = response.data.authorization_url; // Redirect to Paystack payment page//after payment is success i want to return to another page
          } else {
            this.toast.error('Payment initialization failed!');
          }
        },
        (error) => {
          console.error('Error initializing payment:', error);
          this.toast.error(error.error?.errorMessage || 'Something went wrong!');
        },
        () => {
          this.isPaying = false;
        }
      );
    } else {
      this.toast.warn('Please fill in all required fields!');
    }
  }
}
