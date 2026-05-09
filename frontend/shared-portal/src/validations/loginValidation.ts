export const loginValidation = {
  username: {
    required: "Username is required",
  },

  password: {
    required: "Password is required",
    minLength: {
      value: 6,
      message: "Password must be at least 6 characters",
    },
  },
};