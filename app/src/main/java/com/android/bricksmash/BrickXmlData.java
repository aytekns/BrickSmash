package com.android.bricksmash;


public class BrickXmlData 
{
	private int 	m_nRow;
	private int 	m_nColumn;
	private int 	m_nColor;
	private int 	m_nStrength;
	private int 	m_nContent;
	private boolean	m_bExplosive;
	
	public BrickXmlData()
	{
		m_nRow = 0;
		m_nColumn = 0;
		m_nColor = 0;
		m_nStrength = 1;
		m_nContent = -1;
		m_bExplosive = false;
	}
	
	public BrickXmlData(int nRow, int nCol, int nColor, int nStrength)
	{
		m_nRow = nRow;
		m_nColumn = nCol;
		m_nColor = nColor;
		m_nStrength = nStrength;
		m_nContent = -1;
		m_bExplosive = false;
	}
	
	public BrickXmlData(int nRow, int nCol, int nColor, int nStrength, int nContent, boolean bExplosive)
	{
		m_nRow = nRow;
		m_nColumn = nCol;
		m_nColor = nColor;
		m_nStrength = nStrength;
		m_nContent = nContent;
		m_bExplosive = bExplosive;
	}
	
	public int		getRow()		{ return m_nRow;		}
	public int		getColumn()		{ return m_nColumn;		}
	public int 		getColor() 		{ return m_nColor; 		}
	public int 		getStrength()	{ return m_nStrength; 	}
	public int 		getContent()	{ return m_nContent; 	}
	public boolean 	isExplosive() 	{ return m_bExplosive;	}
	
	public void	setRow(int nRow)			{ m_nRow = nRow;			}
	public void setColumn(int nCol)			{ m_nColumn = nCol;			}
	public void	setColor(int nCoolor)		{ m_nColor = nCoolor; 		}
	public void setStrength(int nStrength)	{ m_nStrength = nStrength; 	}
	public void setContent(int nContent)	{ m_nContent = nContent; 	}
	public void setExplosive(boolean bExp)	{ m_bExplosive = bExp;		}
}
