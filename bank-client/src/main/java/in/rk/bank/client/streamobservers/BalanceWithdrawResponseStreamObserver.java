package in.rk.bank.client.streamobservers;

import in.rk.bank.models.BalanceWithdrawResponse;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;

public class BalanceWithdrawResponseStreamObserver implements StreamObserver<BalanceWithdrawResponse> {
    CountDownLatch latch;

    public BalanceWithdrawResponseStreamObserver(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onNext(BalanceWithdrawResponse resp) {
        System.out.println("Async streaming response:"+resp);
    }

    @Override
    public void onError(Throwable t) {
        System.err.println("Error: "+t.getMessage());
        latch.countDown();
    }

    @Override
    public void onCompleted() {
        System.out.println("Server is Done with final response");
        latch.countDown();
    }
}
