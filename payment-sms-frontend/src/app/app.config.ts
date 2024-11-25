import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { heroUsers,heroChevronDown,heroChevronUp, heroArrowRightCircle, heroBars4, heroDocument,
  heroSquares2x2, heroPlusCircle, heroPercentBadge,heroWrenchScrewdriver,heroChartBar, heroCurrencyDollar,heroChatBubbleOvalLeft, heroXMark } from '@ng-icons/heroicons/outline';

import { heroPlaySolid } from '@ng-icons/heroicons/solid';
import { routes } from './app.routes';
import { provideIcons } from '@ng-icons/core';
import { provideHttpClient } from '@angular/common/http';
import { Chart, registerables } from 'chart.js';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
// Register Chart.js components
Chart.register(...registerables);

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(), // Configure HTTP Client
    provideIcons({ heroUsers, heroArrowRightCircle, heroPlaySolid,heroWrenchScrewdriver,heroChartBar,heroChatBubbleOvalLeft, heroBars4,heroChevronDown,heroChevronUp, heroDocument, heroSquares2x2, heroPlusCircle, heroPercentBadge, heroCurrencyDollar, heroXMark }),
    AngularToastifyModule, // Include AngularToastifyModule
    ToastService // Provide Toast Service
  ]
};
