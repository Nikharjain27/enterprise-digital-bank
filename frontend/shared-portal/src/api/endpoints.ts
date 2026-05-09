export const API_ENDPOINTS = {
  AUTH: {
  LOGIN: "/api/v1/auth/login",
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