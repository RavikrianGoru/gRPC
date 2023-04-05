package in.rk.bank.client.streamobservers;

import in.rk.bank.models.TransferResponse;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;

public class TransferResponseStreamObserver implements StreamObserver<TransferResponse> {
    CountDownLatch latch;

    public TransferResponseStreamObserver(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onNext(TransferResponse resp) {
        System.out.println("Status:"+resp.getStatus());
        resp.getAccountsList().stream()
                .map(account->account.getAccountNumber()+":"+account.getAmount())
                .forEach(System.out::println);
        System.out.println("----------------------");
    }

    @Override
    public void onError(Throwable t) {
        System.err.println("Some Error:"+t);
        this.latch.countDown();
    }

    @Override
    public void onCompleted() {
        System.out.println("All Transfer reqs are processed");
        this.latch.countDown();
    }
}
