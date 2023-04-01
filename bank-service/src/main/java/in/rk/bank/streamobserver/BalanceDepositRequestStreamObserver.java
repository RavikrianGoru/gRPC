package in.rk.bank.streamobserver;

import in.rk.bank.db.AccountDB;
import in.rk.bank.models.BalanceDepositRequest;
import in.rk.bank.models.BalanceDepositResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class BalanceDepositRequestStreamObserver implements StreamObserver<BalanceDepositRequest> {
    StreamObserver<BalanceDepositResponse> depositResponseStreamObserver;
    private int accountBalance;

    public BalanceDepositRequestStreamObserver(StreamObserver<BalanceDepositResponse> depositResponseStreamObserver) {
        this.depositResponseStreamObserver = depositResponseStreamObserver;
    }

    @Override
    public void onNext(BalanceDepositRequest req) {
        System.out.println("Each Request:"+req);
        int accountNbr=req.getAccountNumber();
        int amount=req.getAmount();
        if(AccountDB.isAccountAvailable(accountNbr))
        {
            this.accountBalance=AccountDB.addBalance(accountNbr,amount);
        }else
        {
            Status status=Status.FAILED_PRECONDITION.withDescription("Invalid account");
            this.depositResponseStreamObserver.onError(status.asRuntimeException());
            return;
        }

    }

    @Override
    public void onError(Throwable t) {
        System.err.println("Error:"+t.getMessage());
    }

    @Override
    public void onCompleted() {
        BalanceDepositResponse resp=BalanceDepositResponse.newBuilder().setAmount(this.accountBalance).build();
        this.depositResponseStreamObserver.onNext(resp);
        this.depositResponseStreamObserver.onCompleted();
    }
}
