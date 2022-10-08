<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>DB MANAGER</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="format-detection" content="telephone=no">

    <link rel="stylesheet" href="${ctx}/ui/layui/css/layui.css?t=1629677917965" media="all">
    <link rel="stylesheet" href="${ctx}/ui/codemirror/codemirror.css"/>
<%--    <link rel="stylesheet" href="${ctx}/ui/codemirror/hint/show-hint.css"/>--%>
    <link rel="stylesheet" href="${ctx}/ui/css/sql.css"/>
    <link rel="shortcut icon" href="${ctx}/ui/favicon.ico">

    <script>
        var commonCtx = '${ctx}';
        var userName = "${userAccount.name}";
        var userId='${userAccount.id}';
    </script>
</head>
<body>

<div class="layui-layout layui-layout-admin">
    <div class="layui-header">
        <div class="layui-logo layui-hide-xs ">DB MANAGER</div>
        <div class="sql-title" style="padding-left: 200px;margin-top: 5px">
            <div class="sql-select">
                <select id="sqlType" style="float: left">
                    <option value="wz">网址</option>
                    <option value="seal">可信</option>
                </select>
            </div>
            <div class="layui-btn-group" style="float: left;padding-left:50px ">
                <button type="button" id="check" class="layui-btn layui-btn-primary layui-btn-xs"><i class="layui-icon">&#xe623;</i>运行</button>
<%--                <button type="button" id="batchUpdate" class="layui-btn layui-btn-primary layui-btn-xs"><img src="${ctx}/ui/images/update.png" />批量更新</button>--%>
            </div>
        </div>
        <ul class="layui-nav layui-layout-right">
            <li class="navbar">
                <i class="layui-icon layui-icon-spread-left"></i>
            </li>
            <li class="layui-nav-item layui-hide layui-show-md-inline-block">
                <a href="javascript:;">
                    ${userAccount.name}
                </a>
                <dl class="layui-nav-child">
                    <dd><a href="${ctx}/logout">退出</a></dd>
                </dl>
            </li>
        </ul>
    </div>

    <div class="layui-body">
        <!-- 内容主体区域 -->
        <div>
            <textarea id="code"></textarea>
        </div>

        <div id="resContent" class="custom-drag-control closeTab" style="height: 30px">
            <span class="drager top border" data-direct="top"></span>
            <div class="layui-tab" id="resGroup">
                <div class="layui-tab-title" id="drag">
                    <ul id="tabTitle">
                    </ul>
                    <div class="handlerBtn">
                        <div class="hbtn layui-layer-ico layui-layer-max" id="narrowBtn"></div>
                        <div class="hbtn btn-close" id="closeBtn"><i class="layui-icon">&#xe65a;</i></div>
                    </div>
                </div>

                <div class="layui-tab-content" id="result">

                </div>
            </div>
        </div>

    </div>
    <div class="layui-right">
        <div class="sql-db">
            <div class="warpper">
                <select id="storType">
                    <option value="">全部</option>
                    <option value="SAVE">我的</option>
                    <option value="SHARE">分享</option>
                </select>
                <input type="text" id="storTypeInp" placeholder="输入关键字查询">
            </div>
        </div>
        <div class="layui-side-scroll">
            <table class="layui-table sqlRightList" id="sqlRightList" lay-size="sm">

            </table>
            <div class="layui-btn layui-btn-fluid morelist" id="morelist">加载更多</div>
        </div>
    </div>
</div>
<div id="sqlloading"></div>
<script src="${pageContext.request.contextPath}/ui/layui/layui.js?t=1629677917965" charset="utf-8"></script>
<script src="${ctx}/ui/codemirror/codemirror.js"></script>
<script src="${ctx}/ui/codemirror/matchbrackets.js"></script>
<script src="${ctx}/ui/codemirror/lib/formatting.js"></script>
<script src="${ctx}/ui/codemirror/sql.js"></script>
<%--<script src="${ctx}/ui/codemirror/hint/show-hint.js"></script>--%>
<script src="${ctx}/ui/codemirror/hint/sql-hint.js"></script>
<script src="${ctx}/ui/js/coderun.js?V=0.1"></script>
<%--<script src="${ctx}/ui/js/drag.js"></script>--%>
</body>
</html>
