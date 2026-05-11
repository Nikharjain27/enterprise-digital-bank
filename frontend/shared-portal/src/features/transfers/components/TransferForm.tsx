import {
  Alert,
  Box,
  Button,
  MenuItem,
  Paper,
  Snackbar,
  TextField,
  Typography,
} from "@mui/material";

import { useState } from "react";

import { useForm } from "react-hook-form";

import { createTransfer } from "../api/transferApi";

import type {
  TransferRequest,
  TransferResponse,
} from "../types/transfer.types";

import { transferValidation } from "../validations/transferValidation";

const TransferForm = () => {
  const [loading, setLoading] = useState(false);

  const [success, setSuccess] = useState<TransferResponse | null>(null);

  const [error, setError] = useState("");

  const handleSuccessClose = () => {
    setSuccess(null);
  };

  const handleErrorClose = () => {
    setError("");
  };

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<TransferRequest>();

  const onSubmit = async (data: TransferRequest) => {
    try {
      setLoading(true);

      setError("");

      const response = await createTransfer(data);

      setSuccess(response);
    } catch (err) {
      setError("Transfer failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <Paper
        sx={{
          padding: 4,
          borderRadius: 3,
        }}
      >
        <Typography variant="h5" sx={{ mb: 3 }}>
          Transfer Money
        </Typography>

        <Box component="form" onSubmit={handleSubmit(onSubmit)}>
          <TextField
            select
            fullWidth
            label="From Account"
            margin="normal"
            error={!!errors.fromAccount}
            helperText={errors.fromAccount?.message}
            defaultValue=""
            {...register("fromAccount", transferValidation.fromAccount)}
          >
            <MenuItem value="413202257915">413202257915</MenuItem>
          </TextField>

          <TextField
            fullWidth
            label="Beneficiary Account"
            margin="normal"
            error={!!errors.toAccount}
            helperText={errors.toAccount?.message}
            {...register("toAccount", transferValidation.toAccount)}
          />

          <TextField
            fullWidth
            type="number"
            label="Amount"
            margin="normal"
            error={!!errors.amount}
            helperText={errors.amount?.message}
            {...register("amount", transferValidation.amount)}
          />

          <TextField
            fullWidth
            label="description"
            margin="normal"
            error={!!errors.description}
            helperText={errors.description?.message}
            {...register("description", transferValidation.description)}
          />

          <Button
            type="submit"
            variant="contained"
            fullWidth
            size="large"
            disabled={loading}
            sx={{ mt: 3 }}
          >
            {loading ? "Processing..." : "Transfer"}
          </Button>
        </Box>
      </Paper>

      <Snackbar
        open={!!success}
        autoHideDuration={4000}
        onClose={handleSuccessClose}
        anchorOrigin={{
          vertical: "top",
          horizontal: "right",
        }}
      >
        <Alert
          severity="success"
          onClose={handleSuccessClose}
          sx={{ width: "100%" }}
        >
          <strong>Transfer Successful</strong>
          <br />
          Reference: {success?.referenceNumber}
          <br />
          Amount: ₹ {success?.amount}
          <br />
          Updated Balance: ₹ {success?.updatedBalance}
        </Alert>
      </Snackbar>
    </>
  );
};

export default TransferForm;
