<?xml version="1.0" encoding="UTF-8"?>
<rss version="2.0"
     xmlns:content="http://purl.org/rss/1.0/modules/content/"
     xmlns:wfw="http://wellformedweb.org/CommentAPI/"
     xmlns:dc="http://purl.org/dc/elements/1.1/"
     xmlns:atom="http://www.w3.org/2005/Atom"
     xmlns:sy="http://purl.org/rss/1.0/modules/syndication/"
     xmlns:slash="http://purl.org/rss/1.0/modules/slash/"
     xmlns:itunes="http://www.itunes.com/dtds/podcast-1.0.dtd"
     xmlns:podcast="https://podcastindex.org/namespace/1.0"
     xmlns:rawvoice="http://www.rawvoice.com/rawvoiceRssModule/"
     xmlns:georss="http://www.georss.org/georss"
     xmlns:geo="http://www.w3.org/2003/01/geo/wgs84_pos#"
>
    <channel>
        <title>${channel.title}</title>
        <link>${channel.rssFeed}</link>
        <description>${channel.description}</description>
        <language>en-us</language>
        <lastBuildDate>${channel.rssLastUpdated}</lastBuildDate>
        <pubDate>${channel.rssLastUpdated}</pubDate>
        <ttl>1800</ttl>
        ${videos}
    </channel>
</rss>
