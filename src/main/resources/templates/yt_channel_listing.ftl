<!DOCTYPE html>
<html>
<head>
    <title>Recent Videos</title>
    <style>
        .video {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 20px;
        }
        .video-image {
            flex: 1;
        }
        .video-details {
            flex: 3;
            padding: 0 20px;
        }
        .video-link {
            flex: 1;
            text-align: right;
        }
    </style>
</head>
<body>
<#-- show channel title on left and rss link on right -->
<div style="display: flex; justify-content: space-between; align-items: center;">
    <h1>${channel.title}</h1>
    <a href="${channel.rssFeed}"><h1>RSS Feed</h1></a>
</div>
<hr>
<#list videos as video>
    <div class="video">
        <div class="video-image">
            <a href="${video.transcriptUrl}" target="_blank">
                <img src="${video.thumbnailUrl}" alt="Thumbnail">
            </a>
        </div>
        <div class="video-details">
            <h2>${video.title}</h2>
            <p>${video.description}</p>
        </div>
        <div class="video-link">
            <a href="https://youtube.com/watch?v=${video.id}">Watch on YouTube</a>
        </div>
    </div>
</#list>
</body>
</html>
