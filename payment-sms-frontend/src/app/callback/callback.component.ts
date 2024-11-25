import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PaymentService } from '../service/payment.service';

@Component({
  selector: 'app-callback',
  templateUrl: './callback.component.html',
  standalone: true,
  styleUrls: ['./callback.component.css']
})
export class CallbackComponent {
  reference: string | null = null;
  loading: boolean = true;

  constructor(
    private route: ActivatedRoute,
    private paymentService: PaymentService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Get reference from URL
    this.route.queryParams.subscribe(params => {
      this.reference = params['reference'];

      if (this.reference) {
        this.verifyPaymentStatus(this.reference);
      } else {
        // Handle missing reference, could redirect to error page
        this.router.navigate(['/payment-failed']);
      }
    });
  }

  verifyPaymentStatus(reference: string): void {
    // Call the backend API to check the payment status using the reference
    this.paymentService.checkPaymentStatus(reference).subscribe(
      (response) => {
        if (response.status === 'success') {  // Ensure the status matches your backend response
          this.router.navigate(['/success']);
        } else {
          this.router.navigate(['/payment-failed']);
        }
      },
      (error) => {
        console.error('Error during payment status check:', error);
        this.router.navigate(['/payment-failed']);
      }
    );
  }
}
