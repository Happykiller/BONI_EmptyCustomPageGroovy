import com.bonitasoft.engine.api.ProcessAPI

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse
import java.util.logging.Logger;

import com.bonitasoft.engine.api.IdentityAPI;
import com.bonitasoft.engine.api.TenantAPIAccessor;
import org.bonitasoft.console.common.server.page.PageContext;
import org.bonitasoft.console.common.server.page.PageController;
import org.bonitasoft.console.common.server.page.PageResourceProvider;
import org.bonitasoft.engine.session.APISession;

import freemarker.template.Configuration;
import freemarker.template.Template;

import com.happy.lib.Functions;

public class Index implements PageController {

    Boolean debug = false;

	@Override
    public void doGet(HttpServletRequest request, HttpServletResponse response, PageResourceProvider pageResourceProvider, PageContext pageContext) {
        Logger logger = Logger.getLogger("com.bonitasoft.groovy");
        try {
            /////////
            //Build object useful
            /////////
            //Bonita API
            APISession apiSession = pageContext.getApiSession();
            IdentityAPI identityAPI = TenantAPIAccessor.getIdentityAPI(apiSession);
            ProcessAPI processAPI = TenantAPIAccessor.getProcessAPI(apiSession);

            Functions myFunctions = new Functions();
            //model for HTML template
            HashMap<String, Object> model = new HashMap<String, Object>();

            /////////
            //Build general datas useful
            /////////
            //Get url data
            String scheme = request.getScheme();
            String serverName = request.getServerName();
            int serverPort = request.getServerPort();
            String contextPath = request.getContextPath();
            String domaine = scheme + "://" + serverName + ":" + serverPort + contextPath;

            //get the custom page name
            Map parameterMap = request.getParameterMap();
            String customPageName = (String) parameterMap.get("page")[0];

            //Build instance template HTML
            String pathTemplate = System.getProperty("bonita.home") + "/client/tenants/" + apiSession.tenantId + "/work/pages/" + customPageName + "/resources/templates";
            Configuration cfg = new Configuration();
            cfg.setDefaultEncoding("UTF-8");
            cfg.setDirectoryForTemplateLoading(new File(pathTemplate));
            Template template = cfg.getTemplate("customPageTemplate.ftl");

            //form html source
            model.put("customPageName", myFunctions.formatStringToHtml(customPageName));
            model.put("domaine", myFunctions.formatStringToHtml(domaine));

            /////////
            //Build business datas
            /////////

            /////////
            //Send model to template
            /////////
            try {
                template.process(model, response.getWriter());
            } catch (Exception e) {
                String message = "Error in custom page while build response: " + e.getMessage();
                response.getWriter().append(message);
                logger.severe(message);
                logger.severe(e.getStackTrace().toString());
            }
        }catch(Exception e){
            String message = "Error in custom page while void doGet : " + e.getMessage();
            response.getWriter().append(message);
            logger.severe(message);
            logger.severe(e.getStackTrace().toString());
        }
    }
}
