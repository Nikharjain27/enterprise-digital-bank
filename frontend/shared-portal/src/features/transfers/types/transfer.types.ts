export interface TransferRequest {
  fromAccount: string;
  toAccount: string;
  amount: number;
  description: string;
}

export interface TransferResponse {
  referenceNumber: string;
  accountNumber: string;
  transactionType: string;
  amount: number;
  updatedBalance: number;
  message: string;
}