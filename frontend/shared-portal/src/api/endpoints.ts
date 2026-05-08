export const API_ENDPOINTS = {
  AUTH: {
    LOGIN: "/auth/login",
    REFRESH: "/auth/refresh",
    LOGOUT: "/auth/logout",
  },

  ACCOUNTS: {
    GET_ALL: "/accounts",
    GET_BY_ID: (id: string) => `/accounts/${id}`,
  },

  TRANSFERS: {
    CREATE: "/transfers",
    HISTORY: "/transfers/history",
  },

  BENEFICIARIES: {
    GET_ALL: "/beneficiaries",
    VALIDATE: "/beneficiaries/validate",
  },
};