<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title != null ? title : '나무JSP'} - 나무JSP Wiki</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Malgun Gothic', '맑은 고딕', 'Apple SD Gothic Neo', sans-serif;
            line-height: 1.6;
            color: #333;
            background-color: #f8f9fa;
        }

        .header {
            background-color: #2c3e50;
            color: white;
            padding: 1rem 2rem;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }

        .header h1 {
            font-size: 1.5rem;
            margin-bottom: 0.5rem;
        }

        .header h1 a {
            color: white;
            text-decoration: none;
        }

        .header nav {
            margin-top: 0.5rem;
        }

        .header nav a {
            color: #ecf0f1;
            text-decoration: none;
            margin-right: 1.5rem;
            font-size: 0.9rem;
        }

        .header nav a:hover {
            color: #3498db;
        }

        .container {
            max-width: 1200px;
            margin: 2rem auto;
            padding: 0 2rem;
        }

        .wiki-content {
            background: white;
            padding: 2rem;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        .wiki-content h1 {
            font-size: 2rem;
            margin-bottom: 1rem;
            padding-bottom: 0.5rem;
            border-bottom: 2px solid #3498db;
        }

        .wiki-content h2 {
            font-size: 1.5rem;
            margin: 1.5rem 0 1rem 0;
            padding-bottom: 0.3rem;
            border-bottom: 1px solid #ddd;
        }

        .wiki-content h3 {
            font-size: 1.2rem;
            margin: 1.2rem 0 0.8rem 0;
        }

        .wiki-content h4 {
            font-size: 1.1rem;
            margin: 1rem 0 0.6rem 0;
        }

        .wiki-content ul, .wiki-content ol {
            margin-left: 2rem;
            margin-bottom: 1rem;
        }

        .wiki-content li {
            margin-bottom: 0.3rem;
        }

        .wiki-content a.wiki-link {
            color: #3498db;
            text-decoration: none;
        }

        .wiki-content a.wiki-link:hover {
            text-decoration: underline;
        }

        .wiki-content code {
            background-color: #f4f4f4;
            padding: 2px 6px;
            border-radius: 3px;
            font-family: 'Courier New', monospace;
        }

        .wiki-meta {
            margin-top: 2rem;
            padding-top: 1rem;
            border-top: 1px solid #ddd;
            font-size: 0.9rem;
            color: #666;
        }

        .btn {
            display: inline-block;
            padding: 0.5rem 1rem;
            margin: 0.5rem 0.5rem 0.5rem 0;
            background-color: #3498db;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            border: none;
            cursor: pointer;
            font-size: 0.9rem;
        }

        .btn:hover {
            background-color: #2980b9;
        }

        .btn-secondary {
            background-color: #95a5a6;
        }

        .btn-secondary:hover {
            background-color: #7f8c8d;
        }

        .form-group {
            margin-bottom: 1rem;
        }

        .form-group label {
            display: block;
            margin-bottom: 0.3rem;
            font-weight: bold;
        }

        .form-group input,
        .form-group textarea {
            width: 100%;
            padding: 0.5rem;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-family: inherit;
        }

        .form-group textarea {
            min-height: 400px;
            font-family: 'Courier New', monospace;
        }

        .error {
            color: #e74c3c;
            padding: 1rem;
            background-color: #fadbd8;
            border-radius: 4px;
            margin-bottom: 1rem;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin: 1rem 0;
        }

        table th,
        table td {
            padding: 0.75rem;
            border: 1px solid #ddd;
            text-align: left;
        }

        table th {
            background-color: #f8f9fa;
            font-weight: bold;
        }

        table tr:hover {
            background-color: #f1f3f5;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1><a href="<c:url value='/' />">나무JSP</a></h1>
        <nav>
            <a href="<c:url value='/wiki' />">문서 목록</a>
            <a href="<c:url value='/wiki/view/메인페이지' />">메인페이지</a>
        </nav>
    </div>
