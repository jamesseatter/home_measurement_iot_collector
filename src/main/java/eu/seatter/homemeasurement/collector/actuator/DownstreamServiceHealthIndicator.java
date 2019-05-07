//package eu.seatter.homemeasurement.collector.actuator;
//
//import org.springframework.boot.actuate.health.Health;
//import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
//import org.springframework.stereotype.Component;
//import reactor.core.publisher.Mono;
//
///**
// * Created by IntelliJ IDEA.
// * User: jas
// * Date: 07/05/2019
// * Time: 13:59
// */
//@Component
//public class DownstreamServiceHealthIndicator implements ReactiveHealthIndicator {
//
//    @Override
//    public Mono<Health> health() {
//        return checkDownstreamServiceHealth().onErrorResume(
//                ex -> Mono.just(new Health.Builder().down(ex).build())
//        );
//    }
//
//    private Mono<Health> checkDownstreamServiceHealth() {
//        // we could use WebClient to check health reactively
//        return Mono.just(new Health.Builder().up().build());
//    }
//}
