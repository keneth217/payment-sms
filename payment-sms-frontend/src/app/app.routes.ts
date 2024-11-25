import { Routes } from '@angular/router';
import {PaymentSuccessComponent} from "./payment-success/payment-success.component";
import {CallbackComponent} from "./callback/callback.component";

export const routes: Routes = [
  {path:'success',component:PaymentSuccessComponent},
  {path:'payment/callback',component:CallbackComponent}
];
