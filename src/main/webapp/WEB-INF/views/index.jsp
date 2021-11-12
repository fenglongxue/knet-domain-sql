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

    <script>
        var commonCtx = '${ctx}';
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
                <button type="button" id="batchUpdate" class="layui-btn layui-btn-primary layui-btn-xs"><img src="${ctx}/ui/images/update.png" />批量更新</button>
            </div>
        </div>
        <ul class="layui-nav layui-layout-right">
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
            <textarea id="code">update KNET_TEST  t  set t.name='科技有限责任公司董事1' where t.profile_id= '6f49429c34a543b1bdec8a052171580b';
select * from knet_sql_log;
comment on column KNET_SQL_TEST1.sigo is '签报号';
insert into KNET_SQL_TEST1 select * from knet_sql_log;
drop table KNET_SQL_TEST</textarea>
        </div>

        <div id="resContent" class="custom-drag-control">
            <span class="drager top border" data-direct="top"></span>
            <div class="layui-tab" id="resGroup">
                <div class="layui-tab-title" id="drag">
                    <ul id="tabTitle">
                        <%--                    <li class="layui-this">查询</li>--%>
                        <%--                    <li>更新</li>--%>
                        <%--                    <li>表操作</li>--%>
                    </ul>
                    <div class="btn-close" id="closeBtn"><i class="layui-icon">&#x1006;</i></div>
                </div>

                <div class="layui-tab-content" id="result">
                    <%--                <div class="layui-tab-item layui-show">--%>
                    <%--                    <div class="sql-title">--%>
                    <%--                        <div class="layui-btn-group">--%>
                    <%--                            <button type="button" class="layui-btn layui-btn-primary layui-btn-xs"><i class="layui-icon"> &#xe625;</i>更多</button>--%>
                    <%--                            <button type="button" class="layui-btn layui-btn-primary layui-btn-xs"><i class="layui-icon"> &#xe669;</i>刷新</button>--%>
                    <%--                            <button type="button" class="layui-btn layui-btn-primary layui-btn-xs"><i class="layui-icon"> &#xe658;</i>收藏sql</button>--%>
                    <%--                            <button type="button" class="layui-btn layui-btn-primary layui-btn-xs"><i class="layui-icon"> &#xe641;</i>分享sql</button>--%>
                    <%--                            <button type="button" class="layui-btn layui-btn-primary layui-btn-xs"><i class="layui-icon"> &#xe67d;</i>导出</button>--%>
                    <%--                        </div>--%>
                    <%--                    </div>--%>
                    <%--                    <table class="layui-table" id="tabList">--%>
                    <%--                        <thead>--%>
                    <%--                        <tr>--%>
                    <%--                            <th>人物</th>--%>
                    <%--                            <th>民族</th>--%>
                    <%--                            <th>出场时间</th>--%>
                    <%--                            <th>格言</th>--%>
                    <%--                        </tr>--%>
                    <%--                        </thead>--%>
                    <%--                        <tbody>--%>
                    <%--                        <tr>--%>
                    <%--                            <td>贤心</td>--%>
                    <%--                            <td>汉族</td>--%>
                    <%--                            <td>1989-10-14</td>--%>
                    <%--                            <td>人生似修行</td>--%>
                    <%--                        </tr>--%>
                    <%--                        </tbody>--%>
                    <%--                    </table>--%>
                    <%--                </div>--%>
                    <%--                <div class="layui-tab-item">--%>
                    <%--                    <div class="sql-title">--%>
                    <%--                        <span class="sql-tip">受影响数据1000条！</span>--%>
                    <%--                        <div class="layui-btn-group">--%>
                    <%--                            <button type="button" class="layui-btn layui-btn-primary layui-btn-xs"><i class="layui-icon"> &#xe605;</i>执行</button>--%>
                    <%--                        </div>--%>
                    <%--                    </div>--%>
                    <%--                    <table class="layui-table" lay-size="sm">--%>

                    <%--                        <thead>--%>
                    <%--                        <tr>--%>
                    <%--                            <th>ID</th>--%>
                    <%--                            <th>NAME</th>--%>
                    <%--                        </tr>--%>
                    <%--                        </thead>--%>
                    <%--                        <tbody>--%>
                    <%--                        <tr>--%>
                    <%--                            <td>2000011<span style="float:right;">...</span></td>--%>
                    <%--                            <td>徐先念 --><span style="color: green">徐先念1</span><span style="float:right;">...</span></td>--%>

                    <%--                        </tr>--%>

                    <%--                        </tbody>--%>
                    <%--                    </table>--%>
                    <%--                </div>--%>
                    <%--                <div class="layui-tab-item">--%>
                    <%--                    <div class="sql-title" style="border-bottom: 1px solid #eee">--%>
                    <%--                        <div class="layui-btn-group">--%>
                    <%--                            <button type="button" class="layui-btn layui-btn-primary layui-btn-xs"><i class="layui-icon"> &#xe658;</i>收藏sql</button>--%>
                    <%--                            <button type="button" class="layui-btn layui-btn-primary layui-btn-xs"><i class="layui-icon"> &#xe641;</i>分享sql</button>--%>

                    <%--                        </div>--%>
                    <%--                    </div>--%>
                    <%--                    <div class="sql-p10">执行成功：alter table A_20151 add column_2 int</div>--%>
                    <%--                </div>--%>
                </div>
            </div>
        </div>

    </div>
    <div class="layui-right">
        <div class="layui-side-scroll">
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
            <table class="layui-table sqlRightList" id="sqlRightList" lay-size="sm">
<%--                <tr>--%>
<%--                    <td>--%>
<%--                        <span>查询未实名数据</span>--%>
<%--                        <span class="layui-badge layui-bg-red">胡晓彬</span>--%>
<%--                    </td>--%>
<%--                </tr>--%>
<%--                <tr>--%>
<%--                    <td>查询未实名数据</td>--%>
<%--                </tr>--%>
            </table>
            <div class="layui-btn layui-btn-fluid morelist" id="morelist">加载更多</div>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/ui/layui/layui.js?t=1629677917965" charset="utf-8"></script>
<script src="${ctx}/ui/codemirror/codemirror.js"></script>
<script src="${ctx}/ui/codemirror/matchbrackets.js"></script>
<script src="${ctx}/ui/codemirror/sql.js"></script>
<%--<script src="${ctx}/ui/codemirror/hint/show-hint.js"></script>--%>
<script src="${ctx}/ui/codemirror/hint/sql-hint.js"></script>
<script src="${ctx}/ui/js/coderun.js"></script>
<script src="${ctx}/ui/js/drag.js"></script>
</body>
</html>
