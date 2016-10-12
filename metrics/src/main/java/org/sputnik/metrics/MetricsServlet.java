package org.sputnik.metrics;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

@WebServlet(urlPatterns = {"/"})
public class MetricsServlet extends HttpServlet {

    Collection<PublicMetrics> metricsProviders = new ArrayList<>();

    public MetricsServlet() {
        metricsProviders.add(new SystemMetrics());
        metricsProviders.add(new JettyMetrics());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Collection<Metric<?>> metrics = new LinkedHashSet<Metric<?>>();
        for (PublicMetrics provider : metricsProviders) {
            metrics.addAll(provider.metrics());
        }
        resp.setContentType("application/json");
        writeResponse(resp.getOutputStream(), metrics);
    }

    private void writeResponse(ServletOutputStream out, Collection<Metric<?>> metrics) throws IOException {
        out.print("{");
        boolean first = true;
        for (Metric m : metrics) {
            if (!first) {
                out.print(",");
            }
            out.print("\"" + m.getName() + "\":" + m.getValue());
            first = false;
        }
        out.print("}");
    }

}
