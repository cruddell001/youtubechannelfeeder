<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Transcript</title>
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
    <div id="spinner" class="text-center" style="display: none;">
        <div class="spinner-border" role="status">
            <span class="sr-only">Loading...</span>
        </div>
    </div>
    <div id="transcriptContainer"></div>
</div>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script>
    $(document).ready(function() {
        var videoId = "${youtubeItem.id}";

        // First check the cache
        $.getJSON('/cache/transcription/' + videoId, function(cachedData) {
            if (cachedData) {
                console.log("found cached transcript: " + cachedData.title);
                renderTranscript(cachedData);
            } else {
                // If cached data is null, wait for 3 seconds and then fetch new transcript
                $('#spinner').show();
                setTimeout(function() {
                    fetchNewTranscript(videoId);
                }, 3000);
            }
        });

        function fetchNewTranscript(videoId) {
            $.getJSON('/transcribe/' + videoId, function(data) {
                renderTranscript(data);
                $('#spinner').hide();
            });
        }

        function renderTranscript(data) {
            $('#title').text(data.title);
            $('#subtitle').text(data.subtitle);
            $('#thumbnail').attr('src', data.thumbnailUrl);
            $('#thumbnail').attr('alt', data.title);
            $('#author').text('By ' + data.author);
            $('#description').text(data.description);
            $('#youtubeLink').attr('href', data.youtubeUrl);
            $('#lastUpdated').text('Last updated: ' + data.rssDateUpdated);

            var transcriptHtml = '';
            data.texts.forEach(function(text) {
                transcriptHtml += '<div class="transcript-text">';
                if (text.start.toFixed(2) >= 0) {
                    transcriptHtml += '<span class="timestamp">';
                    transcriptHtml += text.start.toFixed(2);
                    transcriptHtml += 's - </span><span>';
                }

                transcriptHtml += text.content;
                transcriptHtml += '</span></div>';
            });
            $('#transcriptContainer').html(transcriptHtml);
        }
    });
</script>
</body>
</html>
