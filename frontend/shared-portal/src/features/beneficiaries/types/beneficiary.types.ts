export interface BeneficiaryRequest {
  customerAccount: string;
  beneficiaryAccount: string;
  beneficiaryName: string;
  ifscCode: string;
}

export interface BeneficiaryResponse {
  beneficiaryAccount: string;
  beneficiaryName: string;
  ifscCode: string;
  active: boolean;
  activationTime: string;
}