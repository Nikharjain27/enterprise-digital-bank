import axiosClient from "../../../api/axiosClient";

import type {
  BeneficiaryRequest,
  BeneficiaryResponse,
} from "../types/beneficiary.types";

export const addBeneficiary =
  async (
    payload: BeneficiaryRequest
  ): Promise<BeneficiaryResponse> => {

    const response =
      await axiosClient.post(
        "/api/v1/accounts/beneficiaries",
        payload
      );

    return response.data;
};