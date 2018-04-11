<%@page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/resources/images/favicon.ico">
    <title>当前浏览器不被支持 - 清源科技</title>
    
    <!--[if lte IE 10]>
        <script type="text/javascript">
            var unsupport = true;
        </script>
    <![endif]-->
 
    <script type="text/javascript">
		if(typeof unsupport == "undefined"){
			location.href = "${pageContext.request.contextPath}/";
		}
   	</script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/unsupport.css">
</head>
<body>
    <div id="container">
        <h1>升级浏览器，升级你的浏览体验</h1>
        <table class="browsers">
            <tr>
                <td>
                    <a class="clearfix" href="https://www.google.com/chrome/browser/" target="_blank">
                        <img src="${pageContext.request.contextPath}/resources/images/browser/chrome.png" alt="Download Chrome">
                        <p>Chrome</p>
                    </a>
                </td>
                <td>
                    <a class="clearfix" href="http://www.firefox.com.cn/" target="_blank">
                        <img src="${pageContext.request.contextPath}/resources/images/browser/firefox.png" alt="Download Firefox">
                        <p>Firefox</p>
                    </a>
                </td>
                <td>
                    <a class="clearfix" href="http://www.apple.com/cn/safari/" target="_blank">
                        <img src="${pageContext.request.contextPath}/resources/images/browser/safari.png" alt="Download Safari">
                        <p>Safari</p>
                    </a>
                </td>
                <td>
                    <a class="clearfix" href="http://www.opera.com/zh-cn" target="_blank">
                        <img src="${pageContext.request.contextPath}/resources/images/browser/opera.png" alt="Download Opera">
                        <p>Opera</p>
                    </a>
                </td>
            </tr>
        </table>
        <div class="suggestion">
            <table>
                <tr>
                    <td width="34%" align="right">
                        <img src="${pageContext.request.contextPath}/resources/images/logo.jpg" alt="smily suggestion">
                    </td>
                    <td class="words" colspan="2" width="66%">
                        <p>您的浏览器好像有点过时了。</p>
                        <p>为了您正常使用WEB后台管理游戏，建议安装以上现代浏览器。</p>
                        <p>如果您正在使用那些所谓的国产浏览器，请您手动切换至极速模式。</p>
                    </td>
                </tr>
            </table>
        </div>
        <div class="footer">
            <p>Copyright&nbsp;&nbsp;&copy;&nbsp;&nbsp;2017&nbsp;&nbsp;清源科技.com </p>
        </div>
    </div>
</body>
</html>