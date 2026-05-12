import {
  Alert,
  Box,
  Button,
  Paper,
  Snackbar,
  TextField,
  Typography,
} from "@mui/material";

import { useState } from "react";

import { useForm } from "react-hook-form";

import { addBeneficiary } from "../api/beneficiaryApi";

import type {
  BeneficiaryRequest,
  BeneficiaryResponse,
} from "../types/beneficiary.types";

import {
  beneficiaryValidation,
} from "../validations/beneficiaryValidation";

const BeneficiaryForm = () => {

  const [success, setSuccess] =
    useState<BeneficiaryResponse | null>(null);

  const [error, setError] =
    useState("");

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<BeneficiaryRequest>();

  const onSubmit = async (
    data: BeneficiaryRequest
  ) => {

    try {

      setError("");

      const response =
        await addBeneficiary(data);

      setSuccess(response);

      reset();

    } catch (err) {

      setError(
        "Failed to add beneficiary"
      );
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

        <Typography
          variant="h5"
          sx={{ mb: 3 }}
        >
          Add Beneficiary
        </Typography>

        <Box
          component="form"
          onSubmit={handleSubmit(onSubmit)}
        >

          <TextField
            fullWidth
            label="Customer Account"
            margin="normal"
            error={
              !!errors.customerAccount
            }
            helperText={
              errors.customerAccount
                ?.message
            }
            {...register(
              "customerAccount",
              beneficiaryValidation
                .customerAccount
            )}
          />

          <TextField
            fullWidth
            label="Beneficiary Account"
            margin="normal"
            error={
              !!errors.beneficiaryAccount
            }
            helperText={
              errors.beneficiaryAccount
                ?.message
            }
            {...register(
              "beneficiaryAccount",
              beneficiaryValidation
                .beneficiaryAccount
            )}
          />

          <TextField
            fullWidth
            label="Beneficiary Name"
            margin="normal"
            error={
              !!errors.beneficiaryName
            }
            helperText={
              errors.beneficiaryName
                ?.message
            }
            {...register(
              "beneficiaryName",
              beneficiaryValidation
                .beneficiaryName
            )}
          />

          <TextField
            fullWidth
            label="IFSC Code"
            margin="normal"
            error={
              !!errors.ifscCode
            }
            helperText={
              errors.ifscCode
                ?.message
            }
            {...register(
              "ifscCode",
              beneficiaryValidation
                .ifscCode
            )}
          />

          <Button
            type="submit"
            variant="contained"
            fullWidth
            sx={{ mt: 3 }}
          >
            Add Beneficiary
          </Button>

        </Box>

      </Paper>

      <Snackbar
        open={!!success}
        autoHideDuration={4000}
        onClose={() =>
          setSuccess(null)
        }
        anchorOrigin={{
          vertical: "top",
          horizontal: "right",
        }}
      >

        <Alert
          severity="success"
          onClose={() =>
            setSuccess(null)
          }
          sx={{ width: "100%" }}
        >

          <strong>
            Beneficiary Added Successfully
          </strong>

          <br />

          Name:
          {" "}
          {success?.beneficiaryName}

          <br />

          Account:
          {" "}
          {success?.beneficiaryAccount}

        </Alert>

      </Snackbar>

      <Snackbar
        open={!!error}
        autoHideDuration={4000}
        onClose={() =>
          setError("")
        }
        anchorOrigin={{
          vertical: "top",
          horizontal: "right",
        }}
      >

        <Alert
          severity="error"
          onClose={() =>
            setError("")
          }
          sx={{ width: "100%" }}
        >
          {error}
        </Alert>

      </Snackbar>

    </>
  );
};

export default BeneficiaryForm;