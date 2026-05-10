import {
  Grid,
  Typography,
  CircularProgress,
  Alert,
} from "@mui/material";

import { useEffect, useState } from "react";

import DashboardLayout from "../../../layouts/DashboardLayout/DashboardLayout";

import { getAccounts } from "../api/accountApi";

import type { Account } from "../types/account.types";

import AccountCard from "../components/AccountCard";

const AccountsPage = () => {

  const [accounts, setAccounts] = useState<Account[]>([]);

  const [loading, setLoading] = useState(true);

  const [error, setError] = useState("");

  useEffect(() => {

    const fetchAccounts = async () => {

      try {

        const data = await getAccounts();

        setAccounts(data);

      } catch (err) {

        setError("Failed to load accounts");

      } finally {

        setLoading(false);
      }
    };

    fetchAccounts();

  }, []);

  if (loading) {
    return <CircularProgress />;
  }

  if (error) {
    return <Alert severity="error">{error}</Alert>;
  }

  return (

    <DashboardLayout>

      <Typography
        variant="h4"
        sx={{ mb: 3 }}
      >
        My Accounts
      </Typography>

      <Grid container spacing={3}>

        {accounts.map((account) => (

          <Grid
            key={account.id}
            size={{ xs: 12, md: 4 }}
          >

            <AccountCard account={account} />

          </Grid>

        ))}

      </Grid>

    </DashboardLayout>
  );
};

export default AccountsPage;