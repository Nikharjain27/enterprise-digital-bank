import {
  Paper,
  Typography,
} from "@mui/material";

import type { Account } from "../types/account.types";

interface Props {
  account: Account;
}

const AccountCard = ({ account }: Props) => {

  return (
    <Paper
      elevation={3}
      sx={{
        padding: 3,
        borderRadius: 3,
      }}
    >

      <Typography variant="h6">
        {account.accountType}
      </Typography>

      <Typography sx={{ mt: 1 }}>
        Account No: {account.accountNumber}
      </Typography>

      <Typography
        variant="h5"
        sx={{
          mt: 2,
          fontWeight: "bold",
        }}
      >
        {account.currency} {account.balance}
      </Typography>

    </Paper>
  );
};

export default AccountCard;