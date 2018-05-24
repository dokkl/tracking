package com.hoon.tracking;

import com.hoon.tracking.history.handler.BoxTrackingHistoryHandler;
import com.hoon.tracking.history.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication
public class TrackingApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrackingApplication.class, args);
	}

	@Bean
	public RouterFunction<ServerResponse> boxTrackingHistoryRoutingFunction(@Autowired BoxTrackingHistoryHandler handler) {
		return nest(path("/tracking"),
				nest(accept(APPLICATION_JSON), route(GET("/{invoiceNumber}"), handler::findBoxTrackingHistory))
				.andRoute(GET("/{shippingCompanyCode}/{invoiceNumber}"), handler::findBoxTrackingHistoryWithShippingComapyCode)
				.andRoute(POST("/").and(contentType(APPLICATION_JSON)), handler::insertBoxTrackingHistory)
		).andNest(accept(APPLICATION_JSON), route(GET("/count/tracking"), handler::getCount));
	}

	@Bean
	public RouterFunction<ServerResponse> testRoutingFunction(@Autowired BoxTrackingHistoryHandler handler) {
		return route(GET("/test"), handler::getTest);
	}
}

@EnableWebFluxSecurity
class SecurityConfig {
	@Bean
	SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) throws Exception {
		return http
				.authorizeExchange()
				.pathMatchers(HttpMethod.GET, "/tracking/**").permitAll()
				.pathMatchers(HttpMethod.DELETE, "/tracking/**").hasRole("ADMIN")
				//.pathMatchers("/posts/**").authenticated()
				//.pathMatchers("/users/{user}/**").access(this::currentUserMatchesPath)
				.anyExchange().permitAll()
				.and()
				.csrf().disable() //이것을 disable하지 않으면 test시에 에러발생(CSRF Token has been associated to this client)
				.build();
	}

	private Mono<AuthorizationDecision> currentUserMatchesPath(Mono<Authentication> authentication, AuthorizationContext context) {
		return authentication
				.map(a -> context.getVariables().get("user").equals(a.getName()))
				.map(granted -> new AuthorizationDecision(granted));
	}

	@Bean
	public MapReactiveUserDetailsService userDetailsRepository() {
		UserDetails user = User.withDefaultPasswordEncoder().username("user").password("password").roles("USER").build();
		UserDetails admin = User.withDefaultPasswordEncoder().username("admin").password("password").roles("USER", "ADMIN").build();
		return new MapReactiveUserDetailsService(user, admin);
	}
}

@Component
@Slf4j
class DataInitializer implements CommandLineRunner {

	private BoxTrackingHistoryRepository repository;

	public DataInitializer(BoxTrackingHistoryRepository repository) {
		this.repository = repository;
	}

	@Override
	public void run(String[] args) {
		log.info("start data initialization  ...");

		BoxTrackingHistory history1 = BoxTrackingHistory.builder()
				.invoiceNumber("505712344406")
				.shippingCompanyCode("D000004")
				.deliveryCode("50")
				.deliveryMessage("배송출발")
				.branchName("서울캠프")
				.branchPhoneNumber("")
				.staffName("김말자")
				.staffMobileNumber("010-1111-3333")
				.scannedAt(LocalDateTime.of(2018, 5, 3, 10, 20, 30))
				.build();

		BoxTrackingHistory history2 = BoxTrackingHistory.builder()
				.invoiceNumber("505712344406")
				.shippingCompanyCode("D000004")
				.deliveryCode("70")
				.deliveryMessage("배송완료")
				.branchName("서울캠프")
				.branchPhoneNumber("")
				.staffName("김말자")
				.staffMobileNumber("010-1111-3333")
				.scannedAt(LocalDateTime.of(2018, 5, 3, 12, 13, 20))
				.build();

		BoxTrackingHistory history3 = BoxTrackingHistory.builder()
				.invoiceNumber("10182815060924")
				.shippingCompanyCode("COUPANG")
				.deliveryCode("70")
				.deliveryMessage("배송완료")
				.branchName("서초1")
				.branchPhoneNumber("")
				.staffName("김쿠팡")
				.staffMobileNumber("010-1111-7777")
				.scannedAt(LocalDateTime.of(2018, 5, 1, 13, 15, 20))
				.build();



		BoxTrackingHistory historyCoupang1 = BoxTrackingHistory.builder()
				.invoiceNumber("10182951047441")
				.shippingCompanyCode("COUPANG")
				.deliveryCode("300")
				.deliveryMessage("집하")
				.branchName("인천3HUB")
				//.branchPhoneNumber("")
				.staffName("AutoScanner")
				//.staffMobileNumber("010-1111-7777")
				.scannedAt(LocalDateTime.of(2018, 5, 2, 10, 43, 39))
				.createdBy("hm_babybong")
				.registeredAt(LocalDateTime.of(2018, 5, 2, 10, 43, 39))
				.invoiceType("DELIVERY")
				.uniqueCode("COUPANG10182951047441")
				//.providerTrackingId("")
				.providerName("HLM")
				.orderId("11000015144657")
				.container(Container.builder().machineCode("9934")
						.invoiceDone("N")
						.build())
				.build();

		BoxTrackingHistory historyCoupang2 = BoxTrackingHistory.builder()
				.invoiceNumber("10182951047441")
				.shippingCompanyCode("COUPANG")
				.deliveryCode("310")
				.deliveryMessage("센터상차")
				.branchName("인천3HUB")
				//.branchPhoneNumber("")
				.staffName("System")
				//.staffMobileNumber("010-1111-7777")
				.scannedAt(LocalDateTime.of(2018, 5, 2, 10, 46, 50))
				.createdBy("hm_babybong")
				.registeredAt(LocalDateTime.of(2018, 5, 2, 10, 46, 50))
				.invoiceType("DELIVERY")
				.uniqueCode("COUPANG10182951047441")
				//.providerTrackingId("")
				.providerName("HLM")
				.orderId("11000015144657")
				.container(Container.builder()
						//.machineCode("9934")
						.containerUsageId("010000342818")
						.invoiceDone("N")
						.containerBarcode("0100010831")
						.containerCode("서울83바5488")
						.containerGroup("LINEHAULTRUCK")
						.containerType("T17")
						.workplace(Workplace.builder()
								.workplaceCode("INCHEON3_H")
								.workplaceName("인천3HUB")
								.workplaceType("HUB")
								.build())
						.build())
				.build();

		BoxTrackingHistory historyCoupang3 = BoxTrackingHistory.builder()
				.invoiceNumber("10182951047441")
				.shippingCompanyCode("COUPANG")
				.deliveryCode("320")
				.deliveryMessage("센터도착")
				.branchName("인천4HUB")
				//.branchPhoneNumber("")
				.staffName("in0032 (안병도)")
				//.staffMobileNumber("010-1111-7777")
				.scannedAt(LocalDateTime.of(2018, 5, 2, 11, 15, 10))
				.createdBy("hm_babybong")
				.registeredAt(LocalDateTime.of(2018, 5, 2, 11, 15, 10))
				.invoiceType("DELIVERY")
				.uniqueCode("COUPANG10182951047441")
				//.providerTrackingId("")
				.providerName("HLM")
				.orderId("11000015144657")
				.container(Container.builder()
						.machineCode("10343")
						.containerUsageId("010000342818")
						.invoiceDone("N")
						.containerBarcode("0100010831")
						.containerCode("서울83바5488")
						.containerGroup("LINEHAULTRUCK")
						.containerType("T17")
						.workplace(Workplace.builder()
								.workplaceCode("5")
								.workplaceName("인천4HUB")
								.workplaceType("HUB")
								.build())
						.build())
				.build();

		BoxTrackingHistory historyCoupang4 = BoxTrackingHistory.builder()
				.invoiceNumber("10182951047441")
				.shippingCompanyCode("COUPANG")
				.deliveryCode("997")
				.deliveryMessage("소터분류")
				.branchName("인천4HUB")
				//.branchPhoneNumber("")
				.staffName("AutoSorter")
				//.staffMobileNumber("010-1111-7777")
				.scannedAt(LocalDateTime.of(2018, 5, 2, 14, 4, 47))
				.createdBy("hm_babybong")
				.registeredAt(LocalDateTime.of(2018, 5, 2, 14, 4, 47))
				.invoiceType("DELIVERY")
				.uniqueCode("COUPANG10182951047441")
				//.providerTrackingId("")
				.providerName("HLM")
				.orderId("11000015144657")
				.container(Container.builder()
						.machineCode("9084")
						.invoiceDone("N")
						.workplace(Workplace.builder()
								.workplaceCode("5")
								.workplaceName("인천4HUB")
								.workplaceType("HUB")
								.build())
						.build())
				.build();

		BoxTrackingHistory historyCoupang5 = BoxTrackingHistory.builder()
				.invoiceNumber("10182951047441")
				.shippingCompanyCode("COUPANG")
				.deliveryCode("330")
				.deliveryMessage("캠프상차")
				.branchName("인천4HUB")
				//.branchPhoneNumber("")
				.staffName("System")
				//.staffMobileNumber("010-1111-7777")
				.scannedAt(LocalDateTime.of(2018, 5, 2, 14, 7, 50))
				.createdBy("hm_babybong")
				.registeredAt(LocalDateTime.of(2018, 5, 2, 14, 7, 50))
				.invoiceType("DELIVERY")
				.uniqueCode("COUPANG10182951047441")
				//.providerTrackingId("")
				.providerName("HLM")
				.orderId("11000015144657")
				.container(Container.builder()
						.invoiceDone("N")
						.containerUsageId("010000342903")
						.containerBarcode("0100056448")
						.containerCode("경기91바9485")
						.containerGroup("LINEHAULTRUCK")
						.containerType("T17")
						.workplace(Workplace.builder()
								.workplaceCode("5")
								.workplaceName("인천4HUB")
								.workplaceType("HUB")
								.build())
						.build())
				.build();

		BoxTrackingHistory historyCoupang6 = BoxTrackingHistory.builder()
				.invoiceNumber("10182951047441")
				.shippingCompanyCode("COUPANG")
				.deliveryCode("500")
				.deliveryMessage("캠프도착")
				.branchName("강서1")
				.branchPhoneNumber("가양동")
				.staffName("임금용")
				.staffMobileNumber("05038293238")
				.scannedAt(LocalDateTime.of(2018, 5, 3, 7, 2, 7))
				.createdBy("cdm_babybong")
				.registeredAt(LocalDateTime.of(2018, 5, 3, 7, 2, 7))
				.invoiceType("DELIVERY")
				.uniqueCode("COUPANG10182951047441")
				//.providerTrackingId("")
				.providerName("CDM")
				.orderId("11000015144657")
				//.coupangMan(CoupangMan.builder().id().build())
				//.walkMan()
				.build();

		BoxTrackingHistory historyCoupang7 = BoxTrackingHistory.builder()
				.invoiceNumber("10182951047441")
				.shippingCompanyCode("COUPANG")
				.deliveryCode("510")
				.deliveryMessage("배송출발")
				.branchName("강서1")
				.branchPhoneNumber("가양동")
				.staffName("최석감")
				.staffMobileNumber("05038290759")
				.scannedAt(LocalDateTime.of(2018, 5, 3, 7, 54, 23))
				.createdBy("cdm_babybong")
				.registeredAt(LocalDateTime.of(2018, 5, 3, 7, 54, 23))
				.invoiceType("DELIVERY")
				.uniqueCode("COUPANG10182951047441")
				//.providerTrackingId("")
				.providerName("CDM")
				.orderId("11000015144657")
				.coupangMan(CoupangMan.builder()
						.id(19953L)
						.name("최석감").build())
				.build();

		BoxTrackingHistory historyCoupang8 = BoxTrackingHistory.builder()
				.invoiceNumber("10182951047441")
				.shippingCompanyCode("COUPANG")
				.deliveryCode("520")
				.deliveryMessage("배송완료")
				.reasonCode("문앞 전달")
				.branchName("강서1")
				.branchPhoneNumber("가양동")
				.staffName("최석감")
				.staffMobileNumber("05038290759")
				.scannedAt(LocalDateTime.of(2018, 5, 3, 15, 43, 8))
				.createdBy("cdm_babybong")
				.registeredAt(LocalDateTime.of(2018, 5, 3, 15, 43, 9))
				.invoiceType("DELIVERY")
				.uniqueCode("COUPANG10182951047441")
				//.providerTrackingId("")
				.providerName("CDM")
				.orderId("11000015144657")
				.walkMan(WalkMan.builder()
						.id(15722L)
						.name("W하희현").build())
				.build();



		this.repository
				.deleteAll()
				.thenMany(
						Flux.just(history1, history2, history3,
								historyCoupang1, historyCoupang2, historyCoupang3, historyCoupang4,
								historyCoupang5, historyCoupang6, historyCoupang7, historyCoupang8)
								.flatMap(history -> this.repository.save(history))
				)
				.log()
				.subscribe(
						null,
						null,
						() -> log.info("done initialization...")
				);

	}

}


