package in.rk.bank.client.streamobservers;

import in.rk.bank.models.BalanceDepositResponse;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;

public class BalanceDepositResponseStreamObserver implements StreamObserver<BalanceDepositResponse> {
    CountDownLatch latch;

    public BalanceDepositResponseStreamObserver(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onNext(BalanceDepositResponse resp) {
        System.out.println("Final Response:"+resp);
    }

    @Override
    public void onError(Throwable t) {
        System.err.println("Error:"+t.getMessage());
        latch.countDown();
    }

    @Override
    public void onCompleted() {
        System.out.println("All request are process and shared final response!");
        latch.countDown();
    }
}
