export const handleApiError = (error: any): string => {
  if (error.response) {
    return error.response.data.message || "API Error";
  }

  if (error.request) {
    return "Network error. Please try again.";
  }

  return "Unexpected error occurred.";
};