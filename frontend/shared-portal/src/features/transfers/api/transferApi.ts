import axiosClient from "../../../api/axiosClient";

import type {
  TransferRequest,
  TransferResponse,
} from "../types/transfer.types";

export const createTransfer = async (
  payload: TransferRequest
): Promise<TransferResponse> => {

  const response = await axiosClient.post(
    "/api/v1/accounts/transfer",
    payload,
    {
      headers: {
        "X-Idempotency-Key":
          crypto.randomUUID(),
      },
    }
  );

  return response.data;
};