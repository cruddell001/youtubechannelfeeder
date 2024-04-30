<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${youtubeItem.title!} Transcript</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.5.2/dist/css/bootstrap.min.css">
    <style>
        body {
            padding-top: 20px;
        }
        .timestamp {
            color: #888;
            font-size: 0.85em;
        }
        .transcript-text {
            margin-bottom: 15px;
        }
    </style>
</head>
<body class="container">
<h1>${youtubeItem.title!}</h1>
<h2 class="text-muted">${youtubeItem.subtitle!}</h2>
<div class="media">
    <img src="${youtubeItem.thumbnailUrl!}" class="mr-3" alt="${youtubeItem.title!}" style="width: 200px;">
    <div class="media-body">
        <h5 class="mt-0">By ${youtubeItem.author!}</h5>
        <p>${youtubeItem.description!}</p>
        <p><a href="${youtubeItem.youtubeUrl}" target="_blank"><img src="/images/watch_on_youtube.png" style="width:75px;" /></a></p>
        <p>Last updated: ${youtubeItem.rssDateUpdated!}</p>
    </div>
</div>

<div>
    <h3>Transcript:</h3>
    <#list transcript.texts as text>
        <div class="transcript-text">
            <span class="timestamp">${text.start?string("0.##")}s - </span>
            <span>${text.content}</span>
        </div>
    </#list>
</div>
</body>
</html>
