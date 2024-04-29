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
</head>
<body>
<div class="container mt-5">
    <h1 class="mb-4">YouTube Channel Search</h1>
    <div class="input-group mb-3">
        <input type="text" id="searchTerm" class="form-control" placeholder="Enter channel name...">
        <div class="input-group-append">
            <button id="searchButton" class="btn btn-primary">Search</button>
        </div>
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
                                $('#results').append(
                                    '<div class="card mb-3">' +
                                    '<div class="row no-gutters">' +
                                    '<div class="col-md-4">' +
                                    '<a href="${baseUrl}/channel/' + channel.channelId + '/videos" target="_blank">' +
                                    '<img src="' + channel.thumbnailUrl + '" class="card-img" alt="' + channel.channelTitle + '" style="width: 100px; height: 100px;">' +
                                    '</a>' +
                                    '</div>' +
                                    '<div class="col-md-8">' +
                                    '<div class="card-body">' +
                                    '<div class="card-title-row">' +
                                    '<h5 class="card-title">' + channel.channelTitle + ' (' + channel.subscribers.toLocaleString() + ' subscribers)</h5>' +
                                    '<a href="' + channel.rssFeed + '" class="btn btn-link" target="_blank">RSS Feed</a>' +
                                    '</div>' +
                                    '<div class="links-row">' +
                                    '<p class="card-text" style="padding-right:8px">' + channel.description + '</p>' +
                                    '<a href="https://youtube.com/' + channel.youtubeUrl + '" target="_blank">View on Youtube</a>' +
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
