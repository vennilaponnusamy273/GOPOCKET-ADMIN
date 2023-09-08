package in.codifi.api.config;

import javax.inject.Singleton;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Singleton
public class ApplicationProperties {

	
	@ConfigProperty(name = "appconfig.file.basepath")
	String fileBasePath;
	@ConfigProperty(name = "appconfig.razorpay.ifsc")
	String razorpayIfscUrl;
	
	@ConfigProperty(name = "appconfig.mail.password")
	String mailPassword;
	@ConfigProperty(name = "appconfig.mail.from")
	String mailFrom;
	@ConfigProperty(name = "appconfig.mail.port")
	String mailPort;
	@ConfigProperty(name = "appconfig.mail.username")
	String mailUserName;
	@ConfigProperty(name = "appconfig.mail.host")
	String mailHost;
	
	@ConfigProperty(name = "appconfig.sms.url")
	String smsUrl;
	@ConfigProperty(name = "appconfig.sms.feedid")
	String smsFeedId;
	@ConfigProperty(name = "appconfig.sms.senderid")
	String smsSenderId;
	@ConfigProperty(name = "appconfig.sms.username")
	String smsUserName;
	@ConfigProperty(name = "appconfig.sms.text")
	String smsText;
	@ConfigProperty(name = "appconfig.sms.password")
	String smsPassword;
	
	@ConfigProperty(name = "appconfig.sms.ref.textone")
	String smsRefFisrtText;
	@ConfigProperty(name = "appconfig.sms.ref.texttwo")
	String smsRefSecondText;
	@ConfigProperty(name = "appconfig.ipv.url.shortner.token")
	String ivrUrlShortnerToken;
	@ConfigProperty(name = "appconfig.bitly.base.url")
	String bitlyBaseUrl;
	
	@ConfigProperty(name = "appconfig.risk.doc.pdf")
	String riskDoc;
	@ConfigProperty(name = "appconfig.backoffice.api")
	String backofficeApi;
	@ConfigProperty(name = "appconfig.backoffice.key")
	String backofficeKey;
}
