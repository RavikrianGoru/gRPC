package in.rk.bank.service.deadline;

import com.google.common.util.concurrent.Uninterruptibles;
import in.rk.bank.db.AccountDB;
import in.rk.bank.models.*;
import in.rk.bank.services.BankServiceGrpc;
import in.rk.bank.streamobserver.BalanceDepositRequestStreamObserver;
import in.rk.bank.streamobserver.TransferRequestStreamObserver;
import io.grpc.Context;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;

public class BankServiceDeadline extends BankServiceGrpc.BankServiceImplBase {
    @Override
    public void checkBalance(BalanceCheckRequest request, StreamObserver<BalanceCheckResponse> responseObserver) {
        System.out.println("---BankServiceDeadline.checkBalance---:"+request.getAccountNumber());
        if (AccountDB.isAccountAvailable(request.getAccountNumber())) {
            BalanceCheckResponse balanceCheckResponse = BalanceCheckResponse.newBuilder()
                    .setAmount(AccountDB.getBalance(request.getAccountNumber()))
                    .build();
            //Simulate time-consuming call
            Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
            responseObserver.onNext(balanceCheckResponse);
            responseObserver.onCompleted();
        } else {
            Status status = Status.FAILED_PRECONDITION.withDescription("Account number is not available");
            responseObserver.onError(status.asRuntimeException());
            return;
        }
    }

    @Override
    public void withdraw(BalanceWithdrawRequest request, StreamObserver<BalanceWithdrawResponse> responseObserver) {
        System.out.println("---BankServiceDeadline.withdraw---:"+request.getAccountNumber());
        int accountNbr = request.getAccountNumber();
        int amount = request.getAmount();
        int finalBalance = AccountDB.getBalance(accountNbr);

        if (AccountDB.isAccountAvailable(accountNbr) && AccountDB.isRequestedAmountAvailable(accountNbr, amount)) {
            for (int i = 0; i < (amount / 10); i++) {
                BalanceWithdrawResponse balanceWithdrawResponse = BalanceWithdrawResponse.newBuilder()
                        .setAmount(10)
                        .build();
                //Simulate time-consuming call
                Uninterruptibles.sleepUninterruptibly(3,TimeUnit.SECONDS);
                if(!Context.current().isCancelled()) {
                    AccountDB.deductBalance(accountNbr, 10);
                    responseObserver.onNext(balanceWithdrawResponse);
                    System.out.println("Delivered $10");
                }else
                {
                    break;
                }
            }
            responseObserver.onCompleted();
            System.out.println("Completed");
        } else {
            Status status = Status.FAILED_PRECONDITION.withDescription("Insufficient Funds:" + finalBalance);
            responseObserver.onError(status.asRuntimeException());
            return;
        }

    }

    @Override
    public StreamObserver<BalanceDepositRequest> deposit(StreamObserver<BalanceDepositResponse> responseObserver) {
        System.out.println("---BankServiceDeadline.deposit---");
        return new BalanceDepositRequestStreamObserver(responseObserver);
    }

    @Override
    public StreamObserver<TransferRequest> transfer(StreamObserver<TransferResponse> responseObserver) {
        System.out.println("---BankServiceDeadline.transfer---");
        return new TransferRequestStreamObserver(responseObserver);
    }
}
