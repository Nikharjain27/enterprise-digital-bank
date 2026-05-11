export const transferValidation = {

  fromAccount: {
    required: "From account required",
  },

  toAccount: {
    required: "Beneficiary account required",
  },

  amount: {
    required: "Amount required",
    min: {
      value: 1,
      message: "Amount must be greater than 0",
    },
  },

  description: {
    required: "Description required",
  },
};