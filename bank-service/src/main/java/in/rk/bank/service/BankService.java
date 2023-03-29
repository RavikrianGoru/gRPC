package in.rk.bank.service;

import in.rk.bank.db.AccountDB;
import in.rk.bank.models.BalanceCheckRequest;
import in.rk.bank.models.BalanceCheckResponse;
import in.rk.bank.services.BankServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class BankService extends BankServiceGrpc.BankServiceImplBase{
    @Override
    public void checkBalance(BalanceCheckRequest request, StreamObserver<BalanceCheckResponse> responseObserver) {
        if(AccountDB.isAccountAvailable(request.getAccountNumber()))
        {
            BalanceCheckResponse balanceCheckResponse=BalanceCheckResponse.newBuilder()
                    .setAmount(AccountDB.getBalance(request.getAccountNumber()))
                    .build();
            responseObserver.onNext(balanceCheckResponse);
            responseObserver.onCompleted();
        }else
        {
            Status status= Status.FAILED_PRECONDITION.withDescription("Account number is not available");
            responseObserver.onError(status.asRuntimeException());
            return;
        }
    }
}
