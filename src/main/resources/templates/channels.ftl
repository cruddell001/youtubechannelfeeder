<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>YouTube Channel Feeder</title>
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <style>
        .spinner-border {
            width: 3rem;
            height: 3rem;
        }
        .card-body {
            display: flex;
            flex-direction: column;
        }
        .card-title-row, .links-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .description-links {
            display: flex;
            flex-direction: column;
            align-items: flex-end;
        }
    </style>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
</head>
<body>
<div class="container mt-5">
    <div class="row">
        <div class="col-md-8">
            <h1 class="mb-4">YouTube Channel Search</h1>
        </div>
        <div class="col-md-4 text-right">
            <a href="https://buymeacoffee.com/youtubechannelfeeder" target="_blank">
                <img src="https://cdn.buymeacoffee.com/buttons/default-orange.png" alt="Buy Me A Coffee" style="height: 51px !important;width: 217px !important;">
            </a>
        </div>
    </div>
    <div class="input-group mb-3">
        <input type="text" id="searchTerm" class="form-control" placeholder="Enter channel name...">
        <div class="input-group-append" style="padding-left: 8px">
            <button id="searchButton" class="btn btn-primary">Search</button>
        </div>
    </div>
    <div id="empty_view" class="text-center" style="padding-top:50px;">
        <p class="text-muted">Can't be bothered to watch YouTube videos all day long?</p>
        <p class="text-muted">Get a RSS feed of your favorite channel and read the transcripts at your own pace.</p>
    </div>
    <div id="loading" class="text-center" style="display: none;">
        <div class="spinner-border text-primary" role="status">
            <span class="sr-only">Loading...</span>
        </div>
    </div>
    <div id="results"></div>
</div>

<script>
    $(document).ready(function() {
        $('#searchTerm').keypress(function(event) {
            if (event.which == 13) {  // Enter key = 13
                event.preventDefault();  // Prevent the default form submit
                $('#searchButton').click();  // Trigger search button click event
            }
        });

        $('#searchButton').click(function() {
            var searchTerm = $('#searchTerm').val();
            if (searchTerm) {
                $('#loading').show();
                $('#results').empty();
                $.ajax({
                    url: '/searchApi?q=' + encodeURIComponent(searchTerm),
                    type: 'GET',
                    success: function(data) {
                        $('#loading').hide();
                        if (data && data.length > 0) {
                            data.forEach(function(channel) {
                                $('#empty_view').hide();
                                $('#results').append(
                                    '<div class="card mb-3" style="height:125px;">' +
                                    '<div class="row no-gutters">' +
                                    '<div class="col-md-4">' +
                                    '<a href="${baseUrl}/channel/' + channel.channelId + '/videos" target="_blank">' +
                                    '<img src="' + channel.thumbnailUrl + '" class="card-img" alt="' + channel.channelTitle + '" style="width: 125px; height: 125px;">' +
                                    '</a>' +
                                    '</div>' +
                                    '<div class="col-md-8">' +
                                    '<div class="card-body">' +
                                    '<div class="card-title-row">' +
                                    '<h5 class="card-title">' + channel.channelTitle + ' (' + channel.subscribers.toLocaleString() + ' subscribers)</h5>' +
                                    '<a href="' + channel.rssFeed + '" class="btn btn-link" target="_blank"><img src="images/rss_button.png" style="width:50px;" /></a>' +
                                    '</div>' +
                                    '<div class="links-row">' +
                                    '<p class="card-text" style="padding-right:8px">' + channel.description + '</p>' +
                                    '<a href="https://youtube.com/' + channel.youtubeUrl + '" target="_blank"><img src="images/watch_on_youtube.png" style="width:75px;" /></a>' +
                                    '</div>' +
                                    '</div>' +
                                    '</div>' +
                                    '</div>' +
                                    '</div>'
                                );
                            });
                        } else {
                            $('#results').append('<p class="text-muted">No results found.</p>');
                        }
                    },
                    error: function() {
                        $('#loading').hide();
                        $('#results').html('<p class="text-danger">An error occurred while searching.</p>');
                    }
                });
            }
        });
    });
</script>
</body>
</html>
