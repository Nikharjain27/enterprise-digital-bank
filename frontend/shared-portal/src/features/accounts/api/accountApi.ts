import axiosClient from "../../../api/axiosClient";

import type { Account } from "../types/account.types";

export const getAccounts = async (): Promise<Account[]> => {

  const response = await axiosClient.get(
    "/api/v1/accounts"
  );

  return response.data;
};