package me.gabcytn.srsly.Problem.Service;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.stereotype.Service;

@Service
public class HtmlSanitizerService {
  public String sanitize(String html) {
    PolicyFactory policyFactory =
        new HtmlPolicyBuilder()
            .allowElements("pre", "code")
            .toFactory()
            .and(Sanitizers.IMAGES)
            .and(Sanitizers.FORMATTING)
            .and(Sanitizers.LINKS)
            .and(Sanitizers.STYLES)
            .and(Sanitizers.BLOCKS);

    return policyFactory.sanitize(html);
  }
}
