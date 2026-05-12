import {
  List,
  ListItem,
  ListItemText,
  Paper,
  Typography,
} from "@mui/material";

const RecentTransactions = () => {

  const mockTransactions = [

    {
      reference: "TXN001",
      amount: "₹1000",
    },

    {
      reference: "TXN002",
      amount: "₹2500",
    },

    {
      reference: "TXN003",
      amount: "₹500",
    },
  ];

  return (

    <Paper
      sx={{
        padding: 3,
        borderRadius: 4,
      }}
    >

      <Typography
        variant="h6"
        sx={{ mb: 2 }}
      >
        Recent Transactions
      </Typography>

      <List>

        {mockTransactions.map(
          (txn) => (

          <ListItem
            key={txn.reference}
          >

            <ListItemText
              primary={txn.reference}
              secondary={txn.amount}
            />

          </ListItem>

        ))}

      </List>

    </Paper>
  );
};

export default RecentTransactions;