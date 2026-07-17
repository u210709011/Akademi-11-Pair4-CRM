import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';

type ApprovalTab = 'pending' | 'approved' | 'rejected';

interface ApprovalRequest {
  requestId: string;
  customer: string;
  type: string;
  requestedBy: string;
  date: string;
  status: 'Pending' | 'Approved' | 'Rejected';
}
// şimdilik static veri gömüldüi data bağlanınca requestleri ayrı dosyaya alıp oradan çekeceğiz

const PENDING_REQUESTS: ApprovalRequest[] = [
  { requestId: 'AP-2041', customer: 'Mert Kaya', type: 'New Customer', requestedBy: 'A. Yılmaz', date: '04/07/2026', status: 'Pending' }
];

const APPROVED_REQUESTS: ApprovalRequest[] = [
  { requestId: 'AP-2038', customer: 'Elif Şahin', type: 'New Customer', requestedBy: 'M. Demir', date: '01/07/2026', status: 'Approved' }
];

const REJECTED_REQUESTS: ApprovalRequest[] = [
  { requestId: 'AP-2035', customer: 'Deniz Koç', type: 'Tariff Discount', requestedBy: 'M. Demir', date: '29/06/2026', status: 'Rejected' }
];

const TAB_LABELS: Record<ApprovalTab, string> = {
  pending: 'Pending',
  approved: 'Approved',
  rejected: 'Rejected'
};

const PANEL_TITLES: Record<ApprovalTab, string> = {
  pending: 'Pending Requests',
  approved: 'Approved Requests',
  rejected: 'Rejected Requests'
};

@Component({
  selector: 'app-approvals',
  imports: [],
  templateUrl: './approvals.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './approvals.component.scss'
})
export class ApprovalsComponent {
  protected readonly tabs: ApprovalTab[] = ['pending', 'approved', 'rejected'];
  protected readonly tabLabels = TAB_LABELS;

  protected readonly activeTab = signal<ApprovalTab>('pending');

  private readonly requestsByTab: Record<ApprovalTab, ApprovalRequest[]> = {
    pending: PENDING_REQUESTS,
    approved: APPROVED_REQUESTS,
    rejected: REJECTED_REQUESTS
  };

  protected readonly panelTitle = computed(() => PANEL_TITLES[this.activeTab()]);
  protected readonly requests = computed(() => this.requestsByTab[this.activeTab()]);

  protected selectTab(tab: ApprovalTab): void {
    this.activeTab.set(tab);
  }
}
