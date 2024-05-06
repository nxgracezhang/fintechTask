from sec_edgar_downloader import Downloader
import os

dl = Downloader("GaTech", "nzhang336@gatech.edu")

# Interested companies: Microsoft, Amazon, Bank of America
ticker = ["MSFT", "AMZN", "BAC"]

# Download 10-K filings for each company in the requested time range
for company in ticker:
    dl.get("10-K", company, after="1995-01-01", before="2023-12-31")