import {
  Grid,
  Typography,
} from "@mui/material";

import DashboardLayout
from "../../layouts/DashboardLayout/DashboardLayout";

import SummaryCard
from "../../components/dashboard/cards/SummaryCard";

import QuickActions
from "../../components/dashboard/actions/QuickActions";

import RecentTransactions
from "../../components/dashboard/widgets/RecentTransactions";

const DashboardPage = () => {

  return (

    <DashboardLayout>

      <Typography
        variant="h4"
        sx={{
          mb: 4,
          fontWeight: 700,
        }}
      >
        Enterprise Digital Bank
      </Typography>

      <Grid
        container
        spacing={3}
      >

        <Grid size={{
          xs: 12,
          md: 4,
        }}>
          <SummaryCard
            title="Total Balance"
            value="₹ 1,25,000"
            subtitle="Across all accounts"
          />
        </Grid>

        <Grid size={{
          xs: 12,
          md: 4,
        }}>
          <SummaryCard
            title="Accounts"
            value="3"
            subtitle="Active accounts"
          />
        </Grid>

        <Grid size={{
          xs: 12,
          md: 4,
        }}>
          <SummaryCard
            title="Transactions"
            value="152"
            subtitle="This month"
          />
        </Grid>

        <Grid size={{
          xs: 12,
          md: 6,
        }}>
          <QuickActions />
        </Grid>

        <Grid size={{
          xs: 12,
          md: 6,
        }}>
          <RecentTransactions />
        </Grid>

      </Grid>

    </DashboardLayout>
  );
};

export default DashboardPage;