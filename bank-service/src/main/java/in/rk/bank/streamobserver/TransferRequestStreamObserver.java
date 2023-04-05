package in.rk.bank.streamobserver;

import in.rk.bank.db.AccountDB;
import in.rk.bank.models.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class TransferRequestStreamObserver implements StreamObserver<TransferRequest> {

    private StreamObserver<TransferResponse> transferResponseStreamObserver;

    public TransferRequestStreamObserver(StreamObserver<TransferResponse> transferResponseStreamObserver) {
        this.transferResponseStreamObserver = transferResponseStreamObserver;
    }

    @Override
    public void onNext(TransferRequest req) {
        int fromAccount = req.getFromAccount();
        int toAccount = req.getToAccount();
        int amount = req.getAmount();
        TransferStatus transferStatus = TransferStatus.FAILED;
        TransferResponse.Builder respBuilder = TransferResponse.newBuilder();

        System.out.println("Each stream call " + fromAccount + "--->" + toAccount);

        if (    fromAccount != toAccount &&
                AccountDB.isAccountAvailable(fromAccount) &&
                AccountDB.isAccountAvailable(toAccount))
        {
            if (AccountDB.getBalance(fromAccount) < amount) {
                System.err.println("Insufficient funds in account");
                Status status = Status.FAILED_PRECONDITION.withDescription("Insufficient funds in account");
                this.transferResponseStreamObserver.onError(status.asRuntimeException());
                return;
            } else {
                AccountDB.deductBalance(fromAccount, amount);
                AccountDB.addBalance(toAccount, amount);
                transferStatus = TransferStatus.SUCCESS;

                respBuilder.addAccounts(Account.newBuilder().setAccountNumber(fromAccount).setAmount(AccountDB.getBalance(fromAccount)).build());
                respBuilder.addAccounts(Account.newBuilder().setAccountNumber(toAccount).setAmount(AccountDB.getBalance(toAccount)).build());
            }
        } else {
            System.err.println("Invalid Accounts");
            Status status = Status.FAILED_PRECONDITION.withDescription("Invalid Accounts");
            this.transferResponseStreamObserver.onError(status.asRuntimeException());
            return;
        }
        respBuilder.setStatus(transferStatus);
        //send over transferResponseStreamObserver
        this.transferResponseStreamObserver.onNext(respBuilder.build());

    }

    @Override
    public void onError(Throwable t) {
        System.err.println("Error: Unexpected error:" + t);
    }

    @Override
    public void onCompleted() {
        //send over transferResponseStreamObserver
        AccountDB.printAccounts();
        this.transferResponseStreamObserver.onCompleted();
    }
}
