# Bitemporal Adventures Part 2: SQL persistence

### Timelines in SQL/Table structure

Representing these in tables would be handy.

We can start with 1-dimension time, which would be similar to what we've seen before:

```
EntityMaster
EID TID
1   1

Timeline
TID TID_SEQ FROM_DATE  TO_DATE  DATA
1   0       1-1        2-1      {:name OldName}
1   1       2-1        Null     {:name NewName}
```

`get company 1 2-15` would become

```sql
SELECT * 
FROM EntityMaster as EM
  LEFT JOIN Timeline as TL on EM.TID = TL.TID
WHERE EM.EID = 1
  and TL.FROM_DATE > '02-15'
  and (TL.TO_DATE is null or TL.TO_DATE <= '02-15')
```

Extending this to a second dimension of time would look like this:

```
EntityMaster
EID   TID
1     1

Timeline
TID TID-SEQ FROM_DATE  TO_DATE   Ref      RefData
1   0       1-15       2-15      TID      3
1   1       2-15       Null      TID      4
3   0       1-1        Null      Company  {:name OldName}
4   0       1-1        2-1       Company  {:name OldName}
4   1       2-1        Null      Company  {:name NewName}
```

`get company 1 2-1 2-14` would translate to something like:

```sql
SELECT *
FROM EntityMaster as EM
 LEFT JOIN Timeline as KD on EM.tid = KD.tid
 LEFT JOIN Timeline as AD on KD.ref_id = AD.tid
WHERE EM.eid = 1
 AND KD.from_date <= '02-14'
 AND (KD.to_date is NULL or KD.to_date > '02-14')
 AND AD.from_date <= '02-01'
 AND (AD.to_date is null or AD.to_date > '02-01')
```

