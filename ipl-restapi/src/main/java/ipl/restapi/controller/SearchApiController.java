package ipl.restapi.controller;

import net.dongliu.requests.Requests;
import net.dongliu.requests.Response;
import net.dongliu.requests.Session;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * <p> Desciption</P>
 *
 * @author 原之安
 * @version V1.0
 * @package ipl.restapi.controller
 * @date 2018/3/31 14:26
 * @since api1.0
 */

@Controller
@CrossOrigin(origins = "*",methods = {GET,POST})
public class SearchApiController {
    Analog_landing analog_landing = new Analog_landing();

    /**
     * 检索API
     * @param searchStr 检索词
     * @param dp 页码
     * @param pn = 10 每页显示条数
     * @param f1 = TI,PN 返回结果字段
     * @return
     */
    @RequestMapping(value = "/getSearch", method = {GET, POST},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Object getUrl(@RequestParam() String searchStr,
                         @RequestParam() String dp,
                         @RequestParam(defaultValue = "10")String pn,
                         @RequestParam(defaultValue = "TI,AB,PA,LS,AN,PN,AD,PD,ZYFT") String f1){

        StringBuilder sb = null;
        // 登陆 Url
        try {
            searchStr = URLEncoder.encode(searchStr,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String dataUrl = "http://172.21.201.131/search/pub/ApiSearch?dp=" + dp + "&pn=" + pn + "&fl=" + f1 + "&q=TI=" +searchStr;

        return analog_landing.ConnectTheNet(dataUrl);
    }

    /**
     * 获取全文
     * @param docPIN 公开号
     * @param docAN 申请号
     * @param docPD 公开日
     * @param mid 索引库号
     * @param fk = TI,AB,CLM,FT,PA,IPC,AN,PN,AU,AD,PD,PR,ADDR,PC,AGC,AGT,QWFT,PCTF,IAN,IPN 返回结果字段
     * @return
     */
    @RequestMapping(value = "/getFullText", method = {GET, POST},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Object getFullText(@RequestParam() String docPIN,
                              @RequestParam() String docAN,
                              @RequestParam() String docPD,
                              @RequestParam() String mid,
                              @RequestParam(defaultValue = "TI,AB,CLM,FT,PA,IPC,AN,PN,AU,AD,PD,PR,ADDR,PC,AGC,AGT,QWFT,PCTF,IAN,IPN") String fk){

        String dataUrl = "http://172.21.201.131/search/pub/ApiDocinfo?fk=" + fk + "&dk=[{\"DCK\":\""+docAN+"@"+docPIN+"@"+docPD+"\",\"MID\":\""+mid+"\"}]";
        System.out.println(dataUrl);
        return analog_landing.ConnectTheNet(dataUrl);
    }

    /**
     * 爬虫方式进行检索
     * @param searchstr
     * @param page
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/search", method = {GET, POST},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Object newSearch(@RequestParam(value = "searchstr")String searchstr,@RequestParam(value = "page")String page) throws UnsupportedEncodingException {
        String url = "http://172.21.201.131/search/user/login";
        String dataurl = "http://172.21.201.131/search/search/result";
        Map<String, Object> params = new HashMap<>();
        params.put("name","webmaster");
        params.put("pwd","dfld1234");

        Map<String, Object> data = new HashMap<>();
        data.put("searchstr", searchstr);
        data.put("page", page);

        Session session = Requests.session();
        Response<String> resp = session.post(url).body(params).send().toTextResponse();

        Response<String> resp2 = session
                .post(dataurl)
                .body(data)
                .send()
                .toTextResponse();
        return re_for_html(resp2.getBody());
    }

    public String re_for_html(String webSource){
        String regex = "<li>\\s+<div[^n][\\s\\S]+?</li>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(webSource);
        String result = "";
        while(matcher.find()){
            result += matcher.group();
        }
        return result;
    }
}
